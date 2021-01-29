//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.djg.kerberos;

import com.google.common.base.Preconditions;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Krb5LoginModule implements JaasModule {
    private static final String LOG_IN_MODULE = "com.sun.security.auth.module.Krb5LoginModule";
    private String principal;
    private String keytab;
    private String serviceName;
    private String contextName;

    private Krb5LoginModule() {
    }

    public String getLogInModuleName() {
        return "com.sun.security.auth.module.Krb5LoginModule";
    }

    public Map<String, Object> getOptions() {
        Map<String, Object> options = new HashMap();
        options.put("principal", this.principal);
        options.put("keyTab", this.keytab);
        options.put("useKeyTab", true);
        options.put("useTicketCache", false);
        options.put("storeKey", true);
        options.put("refreshKrb5Config", true);
        if (this.serviceName != null) {
            options.put("serviceName", this.serviceName);
        }

        return options;
    }

    public String createJaasConf() {
        Preconditions.checkNotNull(this.contextName, "the service context name cannot be null");
        return this.toJaasConf(this.contextName, Collections.singletonList(this));
    }

    public static class Builder {
        private String principal;
        private String keytab;
        private String serviceName;
        private String contextName;

        public Builder() {
        }

        public Krb5LoginModule.Builder principal(String principal) {
            this.principal = principal;
            return this;
        }

        public Krb5LoginModule.Builder keytab(String keytab) {
            this.keytab = keytab;
            return this;
        }

        public Krb5LoginModule.Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Krb5LoginModule.Builder contextName(String contextName) {
            this.contextName = contextName;
            return this;
        }

        public Krb5LoginModule build() {
            Preconditions.checkNotNull(this.principal, "principal cannot be null");
            Preconditions.checkNotNull(this.keytab, "keytab cannot be null");
            Krb5LoginModule logInModule = new Krb5LoginModule();
            logInModule.principal = this.principal;
            logInModule.serviceName = this.serviceName;
            logInModule.contextName = this.contextName;
            logInModule.keytab = this.keytab;
            if (!Files.exists(Paths.get(this.keytab), new LinkOption[0])) {
                throw new IllegalArgumentException("the given keytab file does not exit");
            } else {
                return logInModule;
            }
        }
    }
}
