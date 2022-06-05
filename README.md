# Craft Tweaker Recipe Creator (CTRC)

---


## Description

___

This program allows you to create simple minecraft forge crafting recipes.  
You'll need CraftTweaker. The program is generating the ZenScript (zs) file.

## Manual

___
### Installation

- Download the ZIP file and extract it.   
  There you'll find shell scripts for **Windows** and **Linux**. **macOS** isn't supported yet.   


- Run the file for your OS

### Create a project

- Type the following command:
    
    ~~~shell
    create
    ~~~
  > _A window appears._
- Enter the path to your ZenScript File, your minecraft JAR File (1.x.x.jar) and your mod folder
- Press **Continue**   
  > _An editor instance appears._

### Open an existing project

- Type the following command:
    ~~~shell
    open
    ~~~
  > _A window appears._
- Enter the path to your project file. Valid project file extensions are: _.json, .ctrc, .ctrc.json_

### Editor usage

- Go to **File** and click **New Recipe** and enter the name of your recipe.
  > Now you can create your crafting recipe:
  > - The ComboBox at the top says what the product of the crafting recipe is.
  > - The ComboBox below sets the current selected item
  > - Once you've an item selected, you can click on the crafting grid _(The item should appear there)_

### Save the project

- Go to **File -> Save**
- Select the folder and file name
- Press save
> **NOTE:** The created file is a CTRC-Project file. CraftTweaker cannot read it. To create a ZenScript File go to *Create ZenScript file*

### Create ZenScript file

- Go to **File -> Generate ZS File**
> The program already knows your ZenScript file path, thats why no file chooser will open,
> but the file is saved now.

## Command Documentation

---

> load   
> _Load a project_

> create   
> _Create a new project_

> help   
> _Show a list of commands_

> exit   
> _Exit the program_