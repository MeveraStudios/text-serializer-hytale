# Hytale Component Serializer

A bidirectional serializer for converting between [PaperMC Adventure](https://github.com/PaperMC/adventure) `Component` objects and Hytale's `Message` system.

## Features

- **Bidirectional Conversion**: Seamlessly convert between Adventure Components and Hytale Messages
- **Full Style Support**: Preserves colors, bold, italic, underline, and monospace formatting
- **Translation Support**: Handles translatable components with typed arguments
- **Recursive Children**: Properly serializes and deserializes nested components
- **Type-Safe Arguments**: Supports boolean, numeric, string, and component translation arguments

## Usage

### Basic Serialization

Convert an Adventure Component to a Hytale Message:

```java
// Create an Adventure component
Component component = Component.text("Hello, World!")
    .color(NamedTextColor.RED)
    .decorate(TextDecoration.BOLD);

// Serialize to Hytale Message
Message message = HytaleComponentSerializer.get().serialize(component);
```

### Basic Deserialization

Convert a Hytale Message to an Adventure Component:

```java
// Create a Hytale message
Message message = Message.raw("Hello, World!")
    .color("#FF0000")
    .bold(true);

// Deserialize to Adventure Component
Component component = HytaleComponentSerializer.get().deserialize(message);
```

### Complex Examples

#### Translatable Components with Arguments

```java
// Serialize a translatable component
Component component = Component.translatable("chat.welcome")
    .arguments(
        TranslationArgument.component(Component.text("Player123")),
        TranslationArgument.numeric(42)
    );

Message message = HytaleComponentSerializer.get().serialize(component);
```

#### Nested Components

```java
// Create a complex component with children
Component component = Component.text("Hello ")
    .color(NamedTextColor.YELLOW)
    .append(
        Component.text("World")
            .color(NamedTextColor.GREEN)
            .decorate(TextDecoration.BOLD)
    )
    .append(Component.text("!"));

Message message = HytaleComponentSerializer.get().serialize(component);
```

#### Styled Text

```java
// Apply multiple styles
Component component = Component.text("Important Message")
    .color(TextColor.fromHexString("#FF5555"))
    .decorate(TextDecoration.BOLD)
    .decorate(TextDecoration.ITALIC);

Message message = HytaleComponentSerializer.get().serialize(component);
```

#### Click Events (Links)

```java
// Add a clickable link
Component component = Component.text("Visit our website")
    .color(NamedTextColor.AQUA)
    .clickEvent(ClickEvent.openUrl("https://example.com"));

Message message = HytaleComponentSerializer.get().serialize(component);
```

## Supported Features

### Text Components
- ✅ Raw text content
- ✅ Empty components

### Translatable Components
- ✅ Translation keys
- ✅ Boolean arguments
- ✅ Numeric arguments (int, long, float, double)
- ✅ String arguments
- ✅ Component arguments

### Styling
- ✅ Hex colors (`#RRGGBB`)
- ✅ Named colors (e.g., `red`, `blue`)
- ✅ Bold
- ✅ Italic
- ✅ Underline
- ✅ Monospace (mapped to/from obfuscated)

### Click Events
- ✅ Open URL links

### Structure
- ✅ Nested children
- ✅ Recursive serialization/deserialization

## Style Mapping

| Adventure       | Hytale       | Notes                         |
|-----------------|--------------|-------------------------------|
| `TextColor`     | `color`      | Supports hex and named colors |
| `BOLD`          | `bold`       | Direct mapping                |
| `ITALIC`        | `italic`     | Direct mapping                |
| `UNDERLINED`    | `underlined` | Direct mapping                |
| `OBFUSCATED`    | `monospace`  | Closest equivalent mapping    |
| `STRIKETHROUGH` | ❌            | Not supported in Hytale       |

### Click Events

| Adventure                      | Hytale | Notes          |
|--------------------------------|--------|----------------|
| `ClickEvent.OPEN_URL`          | `link` | Direct mapping |
| `ClickEvent.RUN_COMMAND`       | ❌      | Not supported  |
| `ClickEvent.SUGGEST_COMMAND`   | ❌      | Not supported  |
| `ClickEvent.CHANGE_PAGE`       | ❌      | Not supported  |
| `ClickEvent.COPY_TO_CLIPBOARD` | ❌      | Not supported  |

## Translation Argument Types

The serializer handles all Adventure translation argument types:

```java
// Boolean arguments
TranslationArgument.bool(true);

// Numeric arguments
TranslationArgument.numeric(42);        // int
TranslationArgument.numeric(42L);       // long
TranslationArgument.numeric(3.14f);     // float
TranslationArgument.numeric(3.14);      // double

// Component arguments
TranslationArgument.component(Component.text("Hello"));
```

## Limitations

- **Strikethrough**: Not supported by Hytale's message system
- **Click Events**: Only `OPEN_URL` is supported (commands, suggestions, etc. are not available)
- **Hover Events**: Not supported by Hytale's message system
- **Insertions**: Not supported by Hytale's message system
- **Fonts**: Not supported by Hytale's message system

## Installation

Add the serializer to your project:

```java
// Access the singleton instance
HytaleComponentSerializer serializer = HytaleComponentSerializer.get();
```

## Requirements

- Java 17 or higher (for pattern matching and modern syntax)
- Kyori Adventure API 4.15.0+ (for `TranslationArgument` support)
- Hytale Server Core (for `Message` classes)

## License

This serializer is designed for use with Hytale's server framework and Kyori Adventure library.

## Contributing

When contributing, please ensure:
- Code follows the existing style conventions
- Changes maintain backward compatibility where possible
- New features are documented in this README