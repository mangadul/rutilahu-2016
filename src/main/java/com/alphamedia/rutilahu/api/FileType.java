package com.alphamedia.rutilahu.api;

public enum FileType
{
    JAVA_PROPERTIES("text/plain", true),            // Java resources
    IOS("text/plain", true),                        // iOS resources
    ANDROID("application/xml", true),               // Android resources
    GETTEXT("text/plain", true),                    // GetText .PO/.POT file
    XLIFF("application/xml", true),                 // XLIFF file
    YAML("text/plain", true),                       // Ruby/YAML file
    JSON("application/json", true),                 // generic JSON file
    XML("application/xml", true),                   // generic XML file
    HTML("text/html", true),                        // HTML file
    FREEMARKER("application/octet-stream", false),  // FreeMarker template
    DOCX("application/octet-stream", false),        // DOCX
    DOC("application/octet-stream", false),         // DOC file (Microsoft Word)
    PPTX("application/octet-stream", false),        // PPTX
    XLSX("application/octet-stream", false),        // XLSX
    XLS("application/octet-stream", false),         // XLS
    IDML("application/octet-stream", false),        // IDML
    RESX("application/xml", true),                  // .NET resource (.resx, .resw)
    QT("application/xml", true),                    // Qt Linguist (.TS file)
    CSV("text/csv", true),                          // CSV (Comma-separated values)
    PLAIN_TEXT("text/plain", true),                 // plain text (.txt files)
    PPT("application/octet-stream", false),         // PPT binary file format
    PRES("text/plain", true),                       // Pres resources
    STRINGSDICT("application/xml", true),           // iOS/OSX resources in dictionary format
    JPEG("images/jpeg", false),
    MP4("video/mp4", false),                      // Video mp4
    MPG("video/mpg", false),                      // Video mpg
    /*
    ZIP("application/x-zip", false),                  // zip
    ZIP("application/x-zip-compressed", false),                  // zip
    ZIP("application/octet-stream", false),                  // zip
    ZIP("application/x-compress", false),                  // zip
    ZIP("application/x-compressed", false),                  // zip
    ZIP("application/x-zip", false),                  // zip
    */
    ZIP("application/zip", false);                  // zip

    private final String identifier;
    private final String mimeType;
    private final boolean isTextFormat;

    private static String createIdentifier(String s)
    {
        StringBuilder buf = new StringBuilder();
        String[] parts = s.split("_");

        for (int i = 0; i < parts.length; i++)
            buf.append((i == 0) ? parts[i].toLowerCase() : toTitleCase(parts[i].toLowerCase()));

        return buf.toString();
    }

    private FileType(final String mimeType, final boolean isTextFormat)
    {
        this.identifier = createIdentifier(name());
        this.mimeType = mimeType;
        this.isTextFormat = isTextFormat;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public boolean isTextFormat()
    {
        return isTextFormat;
    }

    public static FileType lookup(final String fileTypeString)
    {
        for (final FileType fileType : FileType.values())
        {
            if (fileType.identifier.equalsIgnoreCase(fileTypeString))
                return fileType;
        }

        return null;
    }

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

}