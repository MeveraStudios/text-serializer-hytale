package studio.mevera.adventure.hytale;

import com.hypixel.hytale.server.core.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HytaleComponentSerializer implements ComponentSerializer<Component, Component, Message> {

    private static final HytaleComponentSerializer INSTANCE = new HytaleComponentSerializer();

    public static HytaleComponentSerializer get() {
        return INSTANCE;
    }

    @Override
    public @NotNull Component deserialize(@NotNull Message input) {
        Component component;

        // Handle raw text or translation
        String rawText = input.getRawText();
        String messageId = input.getMessageId();

        if (rawText != null) {
            component = Component.text(rawText);
        } else if (messageId != null) {
            component = Component.translatable(messageId);
        } else {
            component = Component.empty();
        }

        // Apply styling
        component = applyStyle(component, input);

        // Add children recursively
        List<Message> children = input.getChildren();
        if (!children.isEmpty()) {
            for (Message child : children) {
                component = component.append(deserialize(child));
            }
        }

        return component;
    }

    @Override
    public @NotNull Message serialize(@NotNull Component component) {
        Message message = Message.empty();

        // Handle different component types
        if (component instanceof TextComponent textComponent) {
            String content = textComponent.content();
            if (!content.isEmpty()) {
                message = Message.raw(content);
            }
        } else if (component instanceof TranslatableComponent translatableComponent) {
            message = Message.translation(translatableComponent.key());

            // Handle translation arguments
            List<TranslationArgument> args = translatableComponent.arguments();
            if (!args.isEmpty()) {
                for (int i = 0; i < args.size(); i++) {
                    TranslationArgument arg = args.get(i);
                    Object value = arg.value();

                    // Handle different argument types
                    switch (value) {
                        case Boolean boolValue -> message.param(String.valueOf(i), boolValue);
                        case Number numValue -> {
                            switch (numValue) {
                                case Integer ignored -> message.param(String.valueOf(i), numValue.intValue());
                                case Long ignored -> message.param(String.valueOf(i), numValue.longValue());
                                case Float ignored -> message.param(String.valueOf(i), numValue.floatValue());
                                default -> message.param(String.valueOf(i), numValue.doubleValue());
                            }
                        }
                        case Component componentValue -> message.param(String.valueOf(i), serialize(componentValue));
                        case String stringValue -> message.param(String.valueOf(i), stringValue);
                        default -> message.param(String.valueOf(i), String.valueOf(value));
                    }
                }
            }
        }

        // Apply style
        Style style = component.style();
        applyHytaleStyle(message, style);

        // Handle children recursively
        List<Component> children = component.children();
        if (!children.isEmpty()) {
            for (Component child : children) {
                message.insert(serialize(child));
            }
        }

        return message;
    }

    private Component applyStyle(Component component, Message message) {
        Style.Builder styleBuilder = Style.style();

        // Color
        String color = message.getColor();
        if (color != null) {
            TextColor textColor = parseColor(color);
            if (textColor != null) {
                styleBuilder.color(textColor);
            }
        }

        // Bold (check via FormattedMessage)
        var formattedMessage = message.getFormattedMessage();
        switch (formattedMessage.bold) {
            case True -> styleBuilder.decoration(TextDecoration.BOLD, true);
            case False -> styleBuilder.decoration(TextDecoration.BOLD, false);
            default -> {}
        }

        // Italic
        switch (formattedMessage.italic) {
            case True -> styleBuilder.decoration(TextDecoration.ITALIC, true);
            case False -> styleBuilder.decoration(TextDecoration.ITALIC, false);
            default -> {}
        }

        // Underline
        switch (formattedMessage.underlined) {
            case True -> styleBuilder.decoration(TextDecoration.UNDERLINED, true);
            case False -> styleBuilder.decoration(TextDecoration.UNDERLINED, false);
            default -> {}
        }

        // Monospace (obfuscated as closest match)
        switch (formattedMessage.monospace) {
            case True -> styleBuilder.decoration(TextDecoration.OBFUSCATED, true);
            case False -> styleBuilder.decoration(TextDecoration.OBFUSCATED, false);
            default -> {}
        }

        // Click event (link)
        if (formattedMessage.link != null && !formattedMessage.link.isEmpty()) {
            styleBuilder.clickEvent(ClickEvent.openUrl(formattedMessage.link));
        }

        return component.style(styleBuilder.build());
    }

    @SuppressWarnings("deprecation")
    private void applyHytaleStyle(Message message, Style style) {
        // Color
        TextColor color = style.color();
        if (color != null) {
            message.color(String.format("#%06X", color.value() & 0xFFFFFF));
        }

        // Decorations
        if (style.decoration(TextDecoration.BOLD) == TextDecoration.State.TRUE) {
            message.bold(true);
        } else if (style.decoration(TextDecoration.BOLD) == TextDecoration.State.FALSE) {
            message.bold(false);
        }

        if (style.decoration(TextDecoration.ITALIC) == TextDecoration.State.TRUE) {
            message.italic(true);
        } else if (style.decoration(TextDecoration.ITALIC) == TextDecoration.State.FALSE) {
            message.italic(false);
        }

        // Map obfuscated to monospace as closest equivalent
        if (style.decoration(TextDecoration.OBFUSCATED) == TextDecoration.State.TRUE) {
            message.monospace(true);
        } else if (style.decoration(TextDecoration.OBFUSCATED) == TextDecoration.State.FALSE) {
            message.monospace(false);
        }

        // Click event (only OPEN_URL is supported)
        ClickEvent clickEvent = style.clickEvent();
        if (clickEvent != null && clickEvent.action() == ClickEvent.Action.OPEN_URL) {
            message.link(clickEvent.value());
        }
    }

    private TextColor parseColor(String color) {
        if (color == null || color.isEmpty()) {
            return null;
        }

        // Try hex color
        if (color.startsWith("#")) {
            try {
                return TextColor.fromHexString(color);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        // Try named color
        return NamedTextColor.NAMES.value(color.toLowerCase());
    }
}