package com.gpch.login.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "configuration")
public class ConfigurationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "configuration_id")
    private int id;
    @Column(name = "configname")
    @NotEmpty(message = "*Please provide a configuration name")
    private String configname;
     @Column(name = "configvalue")
    private String configvalue;


    @java.lang.SuppressWarnings("all")
    public static class ConfigurationBuilder {
        @java.lang.SuppressWarnings("all")
        private int id;
        @java.lang.SuppressWarnings("all")
        private String configname;
        @java.lang.SuppressWarnings("all")
        private String configvalue;
 
        @java.lang.SuppressWarnings("all")
        ConfigurationBuilder() {
        }

        @java.lang.SuppressWarnings("all")
        public ConfigurationBuilder id(final int id) {
            this.id = id;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public ConfigurationBuilder configname(final String configname) {
            this.configname = configname;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public ConfigurationBuilder configvalue(final String configvalue) {
            this.configvalue = configvalue;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public ConfigurationModel build() {
            return new ConfigurationModel(id, configname, configvalue);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "Configuration.UserBuilder(id=" + this.id + ", configname=" + this.configname + ", configvalue=" + this.configvalue + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static ConfigurationBuilder builder() {
        return new ConfigurationBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public int getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public String getConfigname() {
        return this.configname;
    }

    @java.lang.SuppressWarnings("all")
    public String getConfigValue() {
        return this.configvalue;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final int id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setConfigname(final String configname) {
        this.configname = configname;
    }

    @java.lang.SuppressWarnings("all")
    public void setConfigValue(final String configvalue) {
        this.configvalue = configvalue;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof ConfigurationModel)) return false;
        final ConfigurationModel configvalue = (ConfigurationModel) o;
        if (!configvalue.canEqual((java.lang.Object) this)) return false;
        if (this.getId() != configvalue.getId()) return false;
        final java.lang.Object this$configname = this.getConfigname();
        final java.lang.Object configvalue$configname = configvalue.getConfigname();
        if (this$configname == null ? configvalue$configname != null : !this$configname.equals(configvalue$configname)) return false;
        final java.lang.Object this$configvalue = this.getConfigValue();
        final java.lang.Object configvalue$configvalue = configvalue.getConfigValue();
        if (this$configvalue == null ? configvalue$configvalue != null : !this$configvalue.equals(configvalue$configvalue)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object configvalue) {
        return configvalue instanceof ConfigurationModel;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getId();
        final java.lang.Object $configname = this.getConfigname();
        result = result * PRIME + ($configname == null ? 43 : $configname.hashCode());
        final java.lang.Object $configvalue = this.getConfigValue();
        result = result * PRIME + ($configvalue == null ? 43 : $configvalue.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "UserReport(id=" + this.getId() + ", configname=" + this.getConfigname() + ", configvalue=" + this.getConfigValue() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public ConfigurationModel(final int id, final String configname, final String configvalue) {
        this.id = id;
        this.configname = configname;
        this.configvalue = configvalue;
    }

    @java.lang.SuppressWarnings("all")
    public ConfigurationModel() {
    }
}
