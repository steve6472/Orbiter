package steve6472.orbiter.rendering.gizmo.shapes;

import org.joml.Vector3f;
import steve6472.orbiter.rendering.gizmo.Gizmo;
import steve6472.orbiter.rendering.gizmo.GizmoPrimitives;
import steve6472.orbiter.rendering.gizmo.alpha.AlphaMultiplier;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
public record HemisphereGizmo(Vector3f position, float radius, int quality, boolean isTop, int color, float lineWidth) implements Gizmo
{
    @Override
    public void create(GizmoPrimitives primitives, AlphaMultiplier alpha)
    {
        int latSegments = quality; // Latitude lines
        int lonSegments = quality + 3; // Longitude lines

        for (int i = 0; i < latSegments; i++)
        {
            float lat1 = (float) (Math.PI / 2 * i / latSegments);
            float lat2 = (float) (Math.PI / 2 * (i + 1) / latSegments);

            if (!isTop)
            {
                lat1 = (float) (-Math.PI / 2 * i / latSegments);
                lat2 = (float) (-Math.PI / 2 * (i + 1) / latSegments);
            }

            for (int j = 0; j < lonSegments; j++)
            {
                float lon1 = (float) (2.0 * Math.PI * j / lonSegments);
                float lon2 = (float) (2.0 * Math.PI * (j + 1) / lonSegments);

                // Convert polar coordinates to Cartesian for both latitude circles
                Vector3f v1 = polarToCartesian(radius, lat1, lon1, isTop ? 0 : -0).add(position);
                Vector3f v2 = polarToCartesian(radius, lat1, lon2, isTop ? 0 : -0).add(position);
                Vector3f v3 = polarToCartesian(radius, lat2, lon1, isTop ? 0 : -0).add(position);
                Vector3f v4 = polarToCartesian(radius, lat2, lon2, isTop ? 0 : -0).add(position);

                if (lineWidth > 0)
                {
                    // Horizontal line on current latitude
                    primitives.line(v1, v2, color, alpha, lineWidth);
                    // Vertical line between latitudes
                    primitives.line(v1, v3, color, alpha, lineWidth);
                }

                if (!isTop)
                {
                    primitives.tri(v1, v2, v3, color, alpha);
                    primitives.tri(v2, v4, v3, color, alpha);
                } else
                {
                    primitives.tri(v1, v3, v2, color, alpha);
                    primitives.tri(v2, v3, v4, color, alpha);
                }
            }
        }
    }

    private static Vector3f polarToCartesian(float radius, float lat, float lon, float offsetY)
    {
        float x = radius * (float) Math.cos(lat) * (float) Math.cos(lon);
        float z = radius * (float) Math.cos(lat) * (float) Math.sin(lon);
        float y = radius * (float) Math.sin(lat) + offsetY;
        return new Vector3f(x, y, z);
    }
}
