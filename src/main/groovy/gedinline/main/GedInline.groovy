package gedinline.main

class GedInline {

    String getVersion() {
        try {

            def properties = new Properties()
            InputStream inputStream1 = getClass().getClassLoader().getResourceAsStream("version.properties");
            properties.load(inputStream1)
            properties.getProperty("version")

        } catch (Exception e) {
            println "Exception while finding version: ${e}"
            throw new RuntimeException('Failed to find version.properties', e)
        }
    }
}
