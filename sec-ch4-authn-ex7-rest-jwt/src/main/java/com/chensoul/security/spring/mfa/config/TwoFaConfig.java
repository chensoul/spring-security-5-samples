package com.chensoul.security.spring.mfa.config;

import com.chensoul.security.spring.mfa.provider.TwoFaProviderType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "providerType")
@JsonSubTypes({
        @Type(name = "TOTP", value = TotpTwoFaConfig.class),
        @Type(name = "SMS", value = SmsTwoFaConfig.class),
        @Type(name = "EMAIL", value = EmailTwoFaConfig.class),
        @Type(name = "BACKUP_CODE", value = BackupCodeTwoFaConfig.class)
})
@Data
public abstract class TwoFaConfig implements Serializable {

    private boolean useByDefault;

    @JsonIgnore
    protected transient boolean serializeHiddenFields;

    @JsonIgnore
    public abstract TwoFaProviderType getProviderType();
}