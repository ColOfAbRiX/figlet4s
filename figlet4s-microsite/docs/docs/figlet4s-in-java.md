---
layout: docs
title: Figlet4s in Java
---
# Figlet4s in Java

If you want to use Figlet4s on Java you can benefit from the thin wrapper developed for this purpose
and bundled in the `figlet4s-java` library. Using a Scala library from Java is very simple and
almost pain-free but there is still some manual conversion you have to do, especially with
collections, and some details you need to know like how to call a Scala `object` or extension
methods. `figlet4s-java` does the tricky bits for you.

Make sure you include in your project the correct dependencies (see the [Setup](setup.html#maven-for-java)
for details).

## Examples

Let's see the same examples we've seen above but this time from a pure Java perspective. This is the
example that uses the builder, in Java:

```java
import com.colofabrix.java.figlet4s.*;
import com.colofabrix.java.figlet4s.options.*;

public class Main {
    public static void main(String[] args) {
        Figlet4s
            .builder()
            .withHorizontalLayout(HorizontalLayout.HORIZONTAL_FITTING)
            .render("Hello, World!")
            .print();
    }
}
```

And this one is the one that sets explicitly each option:

```java
import com.colofabrix.java.figlet4s.*;
import com.colofabrix.java.figlet4s.options.*;

public class Main {
    public static void main(String[] args) {
        RenderOptions options = new RenderOptions(
            Figlet4s.loadFontInternal("standard"),
            120,
            HorizontalLayout.HORIZONTAL_FITTING,
            PrintDirection.LEFT_TO_RIGHT,
            Justification.FONT_DEFAULT);

        Figlet4s
            .renderString("Hello, World!", options)
            .print();
    }
}
```
