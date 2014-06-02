import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Generator {

  static final String strLow = "abcdefghijklmnopqrstuvwxyz";
  static final String strUp = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  static final String strNum = "0123456789";
  static final String strSpec = "!\"£$€[{]}.>,< \\/";
  static final String strPre = "-_-";
  static final String strPost = "`-´\n";
  static int prePostLength = 0;
  static String myString = "";

  static FileWriter fw;
  static File f = new File ("pwdlist_0.txt");
  static int fileCounter = 0;
  static long fileSize = 0;

  static long variantsTenth = 0;
  static long performedVariants = 0;

/**
 * stringBuilder builds the password string and writes it to the file f
 *
 * @param it iteration depth left
 * @param str varialbe the output string is build in
 */
  public static void stringBuilder(int it, String str) {
    if(it == 0) { //string building is finished
      try {
        fw.write(strPre+str+strPost);
        fileSize += prePostLength + str.length();
        if(fileSize > 524288000) { //split at ~500 MBytes
          fileSize = 0;
          fw.flush();
          fw.close();
          f = new File ("pwdlist_" + ++fileCounter + ".txt");
          fw = new FileWriter(f, false); //overwrite if exists
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      performedVariants++;
      if(performedVariants % variantsTenth == 0) {
          System.out.println("Variant: "+performedVariants);
      }
    }
    else {
      for(int i = 0; i < myString.length(); i++) {
        stringBuilder(it-1, str+myString.charAt(i));
      }
    }
  }

/**
 * vari calculates the number of possible variations
 * @return number of possible variations
 */
  public static long vari(int i) {
    long l = Math.round(Math.pow(myString.length(), i));
    variantsTenth = l / 10; //print every 10%
    if(variantsTenth == 0) { variantsTenth = 1; } //don't devide by zero in stringBuilder
    return l;
  }

  public static void main(String[] args) {
    int firstArg=0;
    if (args.length > 0) {
        try {
            firstArg = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Argument " + args[0] + " must be an integer.");
            System.exit(1);
        }
    }
    else {
      System.err.println("Must supply an argument!\npositive number for this many characters\nnegative number for up to this many characters");
      System.exit(1);
    }

    myString = strLow + strUp + strNum + strSpec;
    prePostLength = strPre.length() + strPost.length();
    try {
      fw = new FileWriter (f, false);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if(firstArg > 0) {
      System.out.println(vari(firstArg)+" variations to be calculated");
      stringBuilder(firstArg, "");
    }
    else if(firstArg < 0) {
      firstArg *= -1;
      long before = 0;
      long after = 0;
      long lastDuration = 0;
      long lastVariations = 0;
      for(int i=0; i<=firstArg; i++, performedVariants=0) {
        before = System.nanoTime();
        System.out.println("Iteration "+i);
        lastVariations = vari(i);
        System.out.println(lastVariations+" variations to be calculated");
        stringBuilder(i, "");
        after = System.nanoTime();
        lastDuration = after - before;
        System.out.println("This iteration took "+(lastDuration/1000000)+"ms");
        System.out.println("Predicted duration of next iteration: "+(((lastDuration/lastVariations)*vari(i+1))/1000000)+"ms\n");
      }
      try {
        fw.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    else {
      System.err.println("Argument " + args[0] + " must be positive or negative.");
      System.exit(1);
    }
  }
}
