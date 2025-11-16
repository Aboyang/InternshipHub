package model.enums;

public enum InternshipLevel {
    Basic, Intermediate, Advanced;

    public static InternshipLevel fromString(String s){
        switch(s.toLowerCase()){
            case "basic": return Basic;
            case "intermediate": return Intermediate;
            case "advanced": return Advanced;
            default: throw new IllegalArgumentException("Unknown level: " + s);
        }
    }
}
