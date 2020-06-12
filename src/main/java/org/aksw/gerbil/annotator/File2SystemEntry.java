package org.aksw.gerbil.annotator;

public class File2SystemEntry {

    private int id;
    private String fileName;
    private String email;
    private String systemName;

    public File2SystemEntry(String file, String system, String email, int idInDatabase) {
        this.id = idInDatabase;
        this.fileName = file;
        this.systemName = system;
        this.email = email;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the systemName
     */
    public String getSystemName() {
        return systemName;
    }

    /**
     * @param systemName
     *            the systemName to set
     */
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[File: ");
        builder.append(fileName).append(", System Name: ").append(systemName).append(", Email: ").append(email)
                .append("]");
        return builder.toString();
    }

}
