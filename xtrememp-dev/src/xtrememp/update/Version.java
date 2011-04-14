/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2010 Besmir Beqiri
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package xtrememp.update;

import static xtrememp.util.Utilities.tr;

/**
 *
 * @author Besmir Beqiri
 */
public class Version implements Comparable<Version> {

    public enum VersionType {

        DEV("Dev"),
        BETA("Beta"),
        BETA1("Beta 1"),
        BETA2("Beta 2"),
        RC("RC"),
        RC1("RC 1"),
        RC2("RC 2"),
        FINAL("Final");
        private String vtString;

        VersionType(String vtString) {
            this.vtString = vtString;
        }

        @Override
        public String toString() {
            return vtString;
        }
    }
    /** 
     * Major version number.
     */
    private int majorNumber;
    /** 
     * Minor version number.
     */
    private int minorNumber;
    /** 
     * Micro version number.
     */
    private int microNumber;
    /**
     * Version type.
     */
    private VersionType versionType = VersionType.FINAL;
    /**
     * Codename.
     */
    private String codename;
    /** 
     * Release date.
     */
    private String releaseDate;
    /**
     * Download URL string.
     */
    private String downloadURL;

    /**
     * Empty constructor
     */
    public Version() {
        super();
    }

    /**
     * Default constructor
     * 
     * @param majorNumber
     * @param minorNumber
     * @param microNumber
     * @param versionType
     * @param date
     * @param downloadURL
     */
    public Version(int majorNumber, int minorNumber, int microNumber,
            VersionType versionType, String codename, String date,
            String downloadURL) {
        super();
        this.majorNumber = majorNumber;
        this.minorNumber = minorNumber;
        this.microNumber = microNumber;
        this.versionType = versionType;
        this.codename = codename;
        this.releaseDate = date;
        this.downloadURL = downloadURL;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Version) {
            return compareTo((Version) obj) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public int compareTo(Version version) {
        if (version == null) {
            throw new IllegalArgumentException();
        }

        if (majorNumber > version.getMajorNumber()) {
            return 1;
        } else if (majorNumber < version.getMajorNumber()) {
            return -1;
        } else if (minorNumber > version.getMinorNumber()) {
            return 1;
        } else if (minorNumber < version.getMinorNumber()) {
            return -1;
        } else if (microNumber > version.getMicroNumber()) {
            return 1;
        } else if (microNumber < version.getMicroNumber()) {
            return -1;
        } else {
            return this.versionType.compareTo(version.getVersionType());
        }
    }

    /**
     * Gets major number.
     * 
     * @return the major number
     */
    public int getMajorNumber() {
        return majorNumber;
    }

    /**
     * Gets minor number.
     * 
     * @return the minor number
     */
    public int getMinorNumber() {
        return minorNumber;
    }

    /**
     * Gets micro number.
     * 
     * @return the micro number
     */
    public int getMicroNumber() {
        return microNumber;
    }

    /**
     * Returns version type
     * 
     * @return VersionType
     */
    public VersionType getVersionType() {
        return versionType;
    }

    /**
     * Gets codename.
     *
     * @return the codename
     */
    public String getCodename() {
        return codename;
    }

    /**
     * Gets release date.
     * 
     * @return the release date
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * Gets download URL.
     * 
     * @return the download URL
     */
    public String getDownloadURL() {
        return downloadURL;
    }

    /**
     * Returns version in string format.
     * 
     * @return the version in string format
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(majorNumber);
        sb.append(".");
        sb.append(minorNumber);
        sb.append(".");
        sb.append(microNumber);
        if (versionType != VersionType.FINAL) {
            sb.append(" ");
            sb.append(versionType);
        }
        return sb.toString();
    }

    /**
     * Sets major number.
     * 
     * @param majorNumber the major number
     */
    protected void setMajorNumber(int majorNumber) {
        this.majorNumber = majorNumber;
    }

    /**
     * Sets minor number.
     * 
     * @param minorNumber the minor number
     */
    protected void setMinorNumber(int minorNumber) {
        this.minorNumber = minorNumber;
    }

    /**
     * Sets micro number.
     * 
     * @param microNumber the micro number
     */
    protected void setMicroNumber(int microNumber) {
        this.microNumber = microNumber;
    }

    /**
     * Sets the version type value.
     * 
     * @param versionType the version type
     */
    protected void setVersionType(VersionType versionType) {
        this.versionType = versionType;
    }

    /**
     * Sets release date.
     *
     * @param codename the codename
     */
    protected void setCodename(String codename) {
        this.codename = codename;
    }

    /**
     * Sets release date.
     * 
     * @param date the release date
     */
    protected void setReleaseDate(String date) {
        this.releaseDate = date;
    }

    /**
     * Sets download URL.
     * 
     * @param downloadURL the download URL
     */
    protected void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public static Version getCurrentVersion() {
        int majorNumber = Integer.parseInt(tr("Application.version.majorNumber"));
        int minorNumber = Integer.parseInt(tr("Application.version.minorNumber"));
        int microNumber = Integer.parseInt(tr("Application.version.microNumber"));
        VersionType versionType = VersionType.DEV;
        String codename = tr("Application.version.codename");
        String releaseDate = tr("Application.version.releaseDate");
        String downloadURL = tr("Application.homepage");
        return new Version(majorNumber, minorNumber, microNumber, versionType,
                codename, releaseDate, downloadURL);
    }
}
