package mod.a.util;

import mod.a.util.price.CrateData;
import mod.a.util.price.WellPrice;

import java.util.ArrayList;
import java.util.HashMap;

public class Data {
    public static int numCredits = -1;
    public static HashMap<String, CrateData> cratesMap = new HashMap<>();
    public static ArrayList<CrateData> crates = new ArrayList<>();

    public static WellPrice wellPrice = new WellPrice();
}