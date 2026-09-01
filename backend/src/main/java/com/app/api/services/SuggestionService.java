package com.app.api.services;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Service;


@Service
public class SuggestionService {
    public enum SuggestedAction {
        WARNING,
        SUSPEND_7D,
        SUSPEND_14D,
        SUSPEND_30D,
        BAN
    }

    public enum Severity {
        MINOR,
        MODERATE,
        SEVERE
    }

    
    public enum ViolationType {
        HARASSMENT,
        HATE_SPEECH,
        INAPPROPRIATE_CONTENT,
        SPAM_SCAM,
        PRIVACY_VIOLATION,
        IMPERSONATION,
        TASK_NO_SHOW,
        TASK_POOR_QUALITY,
        TASK_PROPERTY_DAMAGE,
        TASK_UNSAFE_CONDITIONS,
        THREATS_VIOLENCE
    }

    private static final Map<ViolationType, Map<Severity, SuggestedAction>> RULES = buildRules();


    private static Map<ViolationType, Map<Severity, SuggestedAction>> buildRules(){
        Map<ViolationType, Map<Severity, SuggestedAction>> rules = new EnumMap<>(ViolationType.class);

        rules.put(ViolationType.HARASSMENT, tier(
            SuggestedAction.WARNING, SuggestedAction.SUSPEND_7D, SuggestedAction.SUSPEND_30D
        ));

        rules.put(ViolationType.HATE_SPEECH, tier(
            SuggestedAction.WARNING, SuggestedAction.SUSPEND_14D, SuggestedAction.BAN
        ));

        rules.put(ViolationType.INAPPROPRIATE_CONTENT, tier(
            SuggestedAction.WARNING, SuggestedAction.SUSPEND_7D, SuggestedAction.BAN
        ));

        rules.put(ViolationType.SPAM_SCAM, tier(
            SuggestedAction.WARNING, SuggestedAction.SUSPEND_7D, SuggestedAction.BAN
        ));
        rules.put(ViolationType.PRIVACY_VIOLATION, tier(
            SuggestedAction.SUSPEND_7D, SuggestedAction.SUSPEND_30D, SuggestedAction.BAN
        ));

        rules.put(ViolationType.IMPERSONATION, tier(
            SuggestedAction.WARNING, SuggestedAction.SUSPEND_14D, SuggestedAction.BAN
        ));

        rules.put(ViolationType.TASK_NO_SHOW, tier(
            SuggestedAction.WARNING, SuggestedAction.SUSPEND_7D, SuggestedAction.SUSPEND_30D
        ));

        rules.put(ViolationType.TASK_POOR_QUALITY, tier(
            SuggestedAction.SUSPEND_7D, SuggestedAction.SUSPEND_30D, SuggestedAction.BAN
        ));

        rules.put(ViolationType.TASK_PROPERTY_DAMAGE, tier(
            SuggestedAction.SUSPEND_7D, SuggestedAction.SUSPEND_30D, SuggestedAction.BAN
        ));

        rules.put(ViolationType.TASK_UNSAFE_CONDITIONS, tier(
            SuggestedAction.WARNING, SuggestedAction.SUSPEND_7D, SuggestedAction.BAN
        ));

        Map<Severity, SuggestedAction> threats= new EnumMap<>(Severity.class);
        threats.put(Severity.SEVERE, SuggestedAction.BAN);
        rules.put(ViolationType.THREATS_VIOLENCE, threats);





        return rules;

    }

    private static Map<Severity, SuggestedAction> tier(SuggestedAction minor, SuggestedAction moderate, SuggestedAction severe){
        Map<Severity, SuggestedAction> map = new EnumMap<>(Severity.class);
        map.put(Severity.MINOR, minor);
        map.put(Severity.MODERATE, moderate);
        map.put(Severity.SEVERE, severe);
        return map;
    }

    /**
     * Returns the suggested action for a given violation type and severity,
     * or {@code null} if no rule is defined for that pair (e.g. requesting
     * MINOR severity for a zero-tolerance violation type).
     *
     * @param violationType the category of violation
     * @param severity the severity tier assigned by the admin
     * @return the suggested action, or {@code null} if undefined
     */
    public SuggestedAction getSuggestedAction(ViolationType violationType, Severity severity){
        Map<Severity, SuggestedAction> tierMap = RULES.get(violationType);
        return tierMap == null ? null : tierMap.get(severity);
    }
}
