local function createSuggestion(entryIndex, text)
    local position
    local renderOrder

    if (entryIndex == 1) then
        position = {
            type = "anchored",
            anchor = "bottom_left"
        }
    else
        position = {
            type = "relative",
            parent = "entry_"..(entryIndex - 1),
            offset = {0, -8}
        }
        renderOrder = {
            widget = "entry_"..(entryIndex - 1),
            order = "below"
        }
    end

    return
    {
        widget = "orbiter:widget/part/command_suggestion",
        name = "entry_"..entryIndex,
        position = position,
        bounds = {"100%", 9},
        render_order = renderOrder
    }
end

local function propertyChange(widget, changed)
    if (changed.property == "lines") then

        -- Remove old entries
        for _, v in ipairs(widget:getChildrenNames()) do
            widget:removeChild(v)
        end

        -- Create new entries
        local toAdd = {}
        for k, v in ipairs(changed.new_value) do
            toAdd[k] = createSuggestion(k, v)
        end

        widget:addChildren(toAdd)

        -- Update the text here 'cause I'm lazy
        for i, v in ipairs(widget:getChildrenNames()) do
            local child = widget:getChild(v)
            if (child == nil) then return end
            local text = child:getComponent("moondust:text")
            if (text == nil) then return end
            text.text.parts[1].text = changed.new_value[i]
            child:addComponent("moondust:text", text)
        end
    end
end

events.onPropertyChange:register(propertyChange)
