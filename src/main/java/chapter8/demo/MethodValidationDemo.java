package chapter8.demo;

import chapter8.bad.BadDeposit;
import chapter8.bad.BadPeriod;
import chapter8.bad.BadSignature;
import chapter8.good.GoodDeposit;
import chapter8.good.GoodPeriod;
import chapter8.good.GoodSignature;

import java.util.Date;

public final class MethodValidationDemo {

    public static void main(String[] args) {
        item49ParameterValidation();
        item50DefensiveCopies();
        item51Signatures();
    }

    static void item49ParameterValidation() {
        System.out.println("== Item 49: check parameters for validity ==");
        BadDeposit bad = new BadDeposit();
        bad.deposit(-5);
        bad.deposit(Double.NaN);
        System.out.println("bad deposit: balance became " + bad.balance()
                + " (silently accepted negative and NaN)");

        try {
            new GoodDeposit(-1);
        } catch (IllegalArgumentException e) {
            System.out.println("good deposit: constructor rejected negative initial -> "
                    + e.getMessage());
        }
        try {
            new GoodDeposit(100).deposit(0);
        } catch (IllegalArgumentException e) {
            System.out.println("good deposit: deposit(0) rejected -> " + e.getMessage());
        }
    }

    static void item50DefensiveCopies() {
        System.out.println();
        System.out.println("== Item 50: make defensive copies when needed ==");
        Date start = new Date(0);
        Date end = new Date(1000);
        BadPeriod bad = new BadPeriod(start, end);
        bad.getStart().setTime(5000);
        System.out.println("bad period: lengthMillis after mutating getter = "
                + bad.lengthMillis() + " (corrupted)");

        Date gs = new Date(0);
        Date ge = new Date(1000);
        GoodPeriod good = new GoodPeriod(gs, ge);
        gs.setTime(5000);
        good.getStart().setTime(9000);
        System.out.println("good period: lengthMillis stays " + good.lengthMillis()
                + " (copies in ctor and getter shield it)");
    }

    static void item51Signatures() {
        System.out.println();
        System.out.println("== Item 51: design method signatures carefully ==");
        BadSignature bad = new BadSignature();
        boolean r = bad.qualify("alice", "us", true, true, true, false);
        System.out.println("bad signature call: qualify(n,reg,true,true,true,false) -> "
                + r + " (which boolean meant what?)");

        GoodSignature good = new GoodSignature();
        boolean ok = good.qualifies("alice", "us",
                new GoodSignature.Requirement[]{GoodSignature.Requirement.ACTIVE,
                        GoodSignature.Requirement.VERIFIED});
        System.out.println("good signature call: qualifies(n,reg,[ACTIVE,VERIFIED]) -> "
                + ok + " (self-documenting enum)");
    }
}