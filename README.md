# RbxlxParser
RbxlxParser is a commandline program that can parse a .rbxlx Roblox Studio save file into smaller files that are easier to review. This is specifically designed for version control.

Any Lua code will be placed into its own .lua file with the same name as the script it came from.

# Usage
`RbxlxParser.jar SOURCE DESTINATION` - Reads the _SOURCE_ .rbxlx file and parses it, putting all output into the _DESTINATION_ folder.

**NOTE:** If your filepaths contain spaces, be sure to surround them in quotes. (**Ex:** _my folder/output folder_ -> _"my folder/output folder"_)

----

`RbxlxParser.jar help` - Displays the RbxlxParser manual. Use this to find the version number.

**NOTE:** "help" is not case-sensitive.

# Contact
If you have any feature requests or bugs to report, then create a new issue [here](https://github.com/BeachedSiren22/RbxlxParser/issues/new/choose).

If you need to get in contact with the creator of RbxlxParser for any other reason, then you may contact him through [Twitter](https://twitter.com/Warven22).
