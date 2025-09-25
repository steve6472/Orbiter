package steve6472.orbiter.world.particle.components;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import steve6472.core.registry.Key;
import steve6472.core.util.BitUtil;
import steve6472.flare.assets.atlas.AnimationAtlas;
import steve6472.flare.assets.atlas.SpriteAtlas;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.struct.Struct;
import steve6472.flare.ui.textures.SpriteEntry;
import steve6472.flare.ui.textures.animation.SpriteAnimation;
import steve6472.orbiter.rendering.OrbiterPush;
import steve6472.orbiter.world.particle.core.ParticleComponent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by steve6472
 * Date: 9/22/2025
 * Project: Orbiter <br>
 * Yes this one does break ECS, but idc. I really don't
 */
public class FlipbookModel implements ParticleComponent
{
    private SpriteEntry spriteEntry;
    private int[] framesIndex;
    private long[] framesTime;
    private boolean stretchToMaxAge;

    // For struct
    private Vector4f uv;
    private int flags;
    private Vector2f singleSize;
    private Vector2f pixelSize;

    // millisecond resolution
    public long start = -1, end = -1;
    int totalFrames;
    int frameIndex;

    // TODO: make this safe
    public void setup(Key texture, Key atlas, boolean stretchToMaxAge)
    {
        AnimationAtlas animationAtlas = ((SpriteAtlas) FlareRegistries.ATLAS.get(atlas)).getAnimationAtlas();
        this.spriteEntry = animationAtlas.getSprite(texture);
        Objects.requireNonNull(spriteEntry, "No animated sprite for '%s' was found in atlas '%s'".formatted(texture, atlas));
        this.stretchToMaxAge = stretchToMaxAge;
        this.totalFrames = countPossibleFrames(spriteEntry);

        Optional<SpriteAnimation> animationOpt = spriteEntry.data().animation();
        if (animationOpt.isEmpty())
        {
            throw new RuntimeException("die");
        }
        SpriteAnimation animation = animationOpt.get();

        this.uv = spriteEntry.uv();
        this.singleSize = new Vector2f(animation.width(), animation.height());
        this.pixelSize = new Vector2f(spriteEntry.pixelSize());

        this.flags = 0;
        this.flags = BitUtil.setBit(flags, 0, animation.interpolate());
        this.flags = BitUtil.setBit(flags, 1, animation.useOklab());
    }

    public void finishSetup(double maxAge)
    {
        long totalLoopTime = countTotalMilli(spriteEntry);
        Optional<SpriteAnimation> animationOpt = spriteEntry.data().animation();
        if (animationOpt.isEmpty())
        {
            throw new RuntimeException("die");
        }
        SpriteAnimation animation = animationOpt.get();

        framesIndex = new int[totalFrames];
        framesTime = new long[totalFrames];

        if (animation.frames().isEmpty())
        {
            for (int i = 0; i < totalFrames; i++)
            {
                framesIndex[i] = i;
                framesTime[i] = scaleFrame(totalLoopTime / totalFrames, totalLoopTime, maxAge);
            }
        } else
        {
            List<SpriteAnimation.Frame> frames = animation.frames();
            for (int i = 0; i < frames.size(); i++)
            {
                SpriteAnimation.Frame frame = frames.get(i);
                Optional<Long> time = frame.time();
                if (time.isPresent())
                {
                    framesIndex[i] = frame.index();
                    framesTime[i] = scaleFrame(time.get(), totalLoopTime, maxAge);
                } else
                {
                    framesIndex[i] = frame.index();
                    framesTime[i] = scaleFrame(animation.frametime(), totalLoopTime, maxAge);
                }
            }
        }
    }

    public Struct toStruct(long now)
    {
        return OrbiterPush.FLIPBOOK_ANIM_DATA.create(
            uv,

            singleSize,
            getSpriteIndex(),
            getNextFrameIndex(),

            calculateProgress(now),
            flags,
            pixelSize
        );
    }

    /*
     * Animation ticker
     */

    public void tick(long now)
    {
        if (frameIndex + 1 >= totalFrames)
            return;

        float progress = calculateProgress(now);
        if (progress > 1.0)
        {
            increaseFrameIndex(now);
        }
    }

    public void startAnimAfterReset(long now)
    {
        if (start == -1 && end == -1)
        {
            start = now;
            end = now + framesTime[frameIndex];
        }
    }

    private float calculateProgress(long now)
    {
        return (float) (now - start) / (end - start);
    }

    private void increaseFrameIndex(long now)
    {
        frameIndex++;

        start = now;
        end = now + framesTime[frameIndex];
    }

    private int getSpriteIndex()
    {
        if (frameIndex >= totalFrames - 1)
            return framesIndex[totalFrames - 1];
        return framesIndex[frameIndex];
    }

    private int getNextFrameIndex()
    {
        if (frameIndex + 1> totalFrames - 1)
            return framesIndex[totalFrames - 1];
        return framesIndex[frameIndex + 1];
    }

    private long countTotalMilli(SpriteEntry spriteEntry)
    {
        Optional<SpriteAnimation> animationOpt = spriteEntry.data().animation();
        if (animationOpt.isEmpty())
            return 0;
        SpriteAnimation animation = animationOpt.get();
        final long[] totalTime = {0};

        if (animation.frames().isEmpty())
        {
            totalTime[0] = animation.frametime() * totalFrames;
        } else
        {
            for (SpriteAnimation.Frame frame : animation.frames())
            {
                frame.time().ifPresentOrElse(l -> totalTime[0] += l, () -> totalTime[0] += animation.frametime());
            }
        }
        return totalTime[0];
    }

    public long scaleFrame(long frameMillis, long totalFrameMillis, double totalSeconds)
    {
        if (!stretchToMaxAge)
            return frameMillis;

        if (totalFrameMillis <= 0)
            throw new IllegalArgumentException("Total frame milliseconds must be > 0");
        if (totalSeconds <= 0)
            throw new IllegalArgumentException("Total animation time must be > 0");

        double targetTotalMillis = totalSeconds * 1000.0;
        double scale = targetTotalMillis / (double) totalFrameMillis;

        return Math.round(frameMillis * scale);
    }

    private static int countPossibleFrames(SpriteEntry sprite)
    {
        SpriteAnimation animation = sprite.data().animation().orElseThrow();

        if (animation.frames().isEmpty())
        {
            Vector2i size = sprite.pixelSize();
            return (size.x / animation.width()) * (size.y / animation.height());
        } else
        {
            return animation.frames().size();
        }
    }

    @Override
    public void reset()
    {
        start = -1;
        end = -1;
        frameIndex = 0;
    }

    @Override
    public String toString()
    {
        return "FlipbookModel{" + "spriteEntry=" + spriteEntry + ", framesIndex=" + Arrays.toString(framesIndex) + ", framesTime=" + Arrays.toString(framesTime) + ", stretchToMaxAge=" + stretchToMaxAge + ", uv=" + uv + ", flags=" + flags + ", singleSize=" + singleSize + ", pixelSize=" + pixelSize + ", start=" + start + ", end=" + end + ", totalFrames=" + totalFrames + ", frameIndex=" + frameIndex + '}';
    }
}
