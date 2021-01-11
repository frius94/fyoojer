class Word {
    private String motherLanguage;
    private String foreignLanguage;

    Word(String motherLanguage, String foreignLanguage) {
        this.motherLanguage = motherLanguage;
        this.foreignLanguage = foreignLanguage;
    }

    String getMotherLanguage() {
        return motherLanguage;
    }

    String getForeignLanguage() {
        return foreignLanguage;
    }
}
