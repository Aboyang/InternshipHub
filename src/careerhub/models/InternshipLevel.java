package careerhub.models;

/**
 * Represents the academic difficulty or experience level expected for an internship.
 * This enumeration is used by the Internship class to enforce eligibility rules
 * for students when applying for internship opportunities.
 *
 * <p>According to assignment rules:</p>
 * <ul>
 *     <li>Year 1–2 students may apply only for {@code BASIC} level internships.</li>
 *     <li>Year 3–4 students may apply for any level:
 *         {@code BASIC}, {@code INTERMEDIATE}, or {@code ADVANCED}.</li>
 * </ul>
 */
public enum InternshipLevel {

    /** Suitable for Year 1–2 students; minimal prerequisite knowledge. */
    BASIC,

    /** Suitable for students with intermediate coursework or technical background. */
    INTERMEDIATE,

    /** Intended for more advanced students with deeper specialization. */
    ADVANCED
}
