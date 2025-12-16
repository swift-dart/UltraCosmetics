# UltraCosmetics Modifications Guide

## Changes Made:

### 1. NEW FILE: CosmeticsHotbarListener.java
Location: core/src/main/java/be/isach/ultracosmetics/listeners/CosmeticsHotbarListener.java
 Already created - This handles the hotbar item

### 2. MODIFY: UltraCosmetics.java
Location: core/src/main/java/be/isach/ultracosmetics/UltraCosmetics.java

#### Import the new listener (add to imports at top):
`java
import be.isach.ultracosmetics.listeners.CosmeticsHotbarListener;
`

#### Register the listener (find where other listeners are registered, around line with 'pluginManager.registerEvents'):
`java
// Add this line with the other listener registrations
pluginManager.registerEvents(new CosmeticsHotbarListener(this), this);
`

#### Comment out command registration (find 'commandManager = new CommandManager'):
`java
// COMMENTED OUT - Using hotbar item instead
// commandManager = new CommandManager(this);
`

### 3. GUI Item Configuration Locations:

**Main Menu:**
- File: core/src/main/java/be/isach/ultracosmetics/menu/menus/MenuMain.java
- This is where you can edit what items appear in the main cosmetics GUI

**Category Menus (Gadgets, Pets, Mounts, etc.):**
- Location: core/src/main/java/be/isach/ultracosmetics/menu/menus/
- Files like: MenuGadgets.java, MenuPets.java, MenuMounts.java, etc.

**Available Cosmetic Types:**
- File: core/src/main/java/be/isach/ultracosmetics/cosmetics/type/
- Contains all the individual cosmetic types you can enable/disable

**Config Files for Items:**
- Location: core/src/main/resources/
- Files: categories.yml, config.yml
- Edit these to enable/disable specific cosmetics

### 4. Building the Plugin:

From the UltraCosmetics directory, run:
`
gradlew clean build
`

The compiled jar will be in: build/libs/

## Summary:
1.  Created hotbar listener that gives permanent cosmetics menu item in slot 0
2.  Need to register listener in main class
3.  Need to disable command manager
4. Then build and test!
