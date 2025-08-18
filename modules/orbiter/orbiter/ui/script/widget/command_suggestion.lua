--#include moondust:widget/util

local function mouseEnter(widget)
    if widget:isEnabled() and not widget:internalStates().hovered then
        changeCurrentSprite(widget, "selected")
    end
end

local function mouseLeave(widget)
    if widget:isEnabled() and widget:internalStates().hovered then
        changeCurrentSprite(widget, "normal")
    end
end

local function mouseRelease(widget)
    if not buttonTest(widget) then return end

    widget:sendCommand("orbiter:select_command_suggestion", widget:getName())
end

events.onMouseEnter:register(mouseEnter)
events.onMouseLeave:register(mouseLeave)
events.onMouseRelease:register(mouseRelease)
