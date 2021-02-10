---
layout: docs
title: Figlet Glossary
permalink: docs/glossary
---
# Figlet Glossary

Figlet4s defines several concepts that broadly correspond to the ones defined in the [The FIGfont
Version 2 FIGfont and FIGdriver Standard](../../docs/figfont-reference/) but in this library they
might assume a more specific meaning.

##### FIGfont

A FIGlet Font is a map of characters to their FIG-representation, and the typographic settings used
to display them.

##### FIGcharacter

It's a single FIGlet character, part of a FIGfont, that maps a single `Char` to its FIGlet
representation, and it's composed of SubLines/SubColumns.

##### FIGheader

FIGLettering Font file header that contains the raw configuration settings for the FIGfont.

##### FIGure

A FIGure is `String` rendered with a specific FIGfont ultimately built by concatenating and merging
FIGcharacters following a specific layout.

##### SubLine and SubColumn

Represents the SubLines/SubColumns in Figlet which are the String that compose each line/column of
the FIGure or of a FIGcharacter.
