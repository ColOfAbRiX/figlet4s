package com.colofabrix.scala.figlet4s.figfont

import scala.collection.immutable.ListMap

object StandardFont {

  case class Header(
      signature: String = "flf2a",
      hardblank: String = "$",
      height: String = "6",
      baseline: String = "5",
      maxLength: String = "16",
      oldLayout: String = "15",
      commentLines: String = "11",
      printDirection: String = "0",
      fullLayout: String = "24463",
      codetagCount: String = "4",
  ) {
    // format: off
    val toList: List[String] = List(
      signature, hardblank, height, baseline, maxLength, oldLayout,
      commentLines, printDirection, fullLayout, codetagCount)
    // format: on

    def toLine: String =
      toList.mkString(" ").replaceFirst(" ", "")

    def noPrintDirection: String =
      toList.dropRight(3).mkString(" ").replaceFirst(" ", "")

    def noFullLayout: String =
      toList.dropRight(2).mkString(" ").replaceFirst(" ", "")

    def noCodetagCount: String =
      toList.dropRight(1).mkString(" ").replaceFirst(" ", "")
  }

  val header: Header = Header()

  val comment: String =
    """|Standard by Glenn Chappell & Ian Chai 3/93 -- based on Frank's .sig
       |Includes ISO Latin-1
       |figlet release 2.1 -- 12 Aug 1994
       |Modified for figlet 2.2 by John Cowan <cowan@ccil.org>
       |  to add Latin-{2,3,4,5} support (Unicode U+0100-017F).
       |Permission is hereby given to modify this font, as long as the
       |modifier's name is placed on a comment line.
       |
       |Modified by Paul Burton <solution@earthlink.net> 12/96 to include new parameter
       |supported by FIGlet and FIGWin.  May also be slightly modified for better use
       |of new full-width/kern/smush alternatives, but default output is NOT changed.""".stripMargin

  val characters: ListMap[String, String] =
    ListMap(
      "032" -> """| $@
                  | $@
                  | $@
                  | $@
                  | $@
                  | $@@""".stripMargin,
      "033" -> """|  _ @
                  | | |@
                  | | |@
                  | |_|@
                  | (_)@
                  |    @@""".stripMargin,
      "034" -> """|  _ _ @
                  | ( | )@
                  |  V V @
                  |   $  @
                  |   $  @
                  |      @@""".stripMargin,
      "035" -> """|    _  _   @
                  |  _| || |_ @
                  | |_  ..  _|@
                  | |_      _|@
                  |   |_||_|  @
                  |           @@""".stripMargin,
      "036" -> """|   _  @
                  |  | | @
                  | / __)@
                  | \__ \@
                  | (   /@
                  |  |_| @@""".stripMargin,
      "037" -> """|  _  __@
                  | (_)/ /@
                  |   / / @
                  |  / /_ @
                  | /_/(_)@
                  |       @@""".stripMargin,
      "038" -> """|   ___   @
                  |  ( _ )  @
                  |  / _ \/\@
                  | | (_>  <@
                  |  \___/\/@
                  |         @@""".stripMargin,
      "039" -> """|  _ @
                  | ( )@
                  | |/ @
                  |  $ @
                  |  $ @
                  |    @@""".stripMargin,
      "040" -> """|   __@
                  |  / /@
                  | | | @
                  | | | @
                  | | | @
                  |  \_\@@""".stripMargin,
      "041" -> """| __  @
                  | \ \ @
                  |  | |@
                  |  | |@
                  |  | |@
                  | /_/ @@""".stripMargin,
      "042" -> """|       @
                  | __/\__@
                  | \    /@
                  | /_  _\@
                  |   \/  @
                  |       @@""".stripMargin,
      "043" -> """|        @
                  |    _   @
                  |  _| |_ @
                  | |_   _|@
                  |   |_|  @
                  |        @@""".stripMargin,
      "044" -> """|    @
                  |    @
                  |    @
                  |  _ @
                  | ( )@
                  | |/ @@""".stripMargin,
      "045" -> """|        @
                  |        @
                  |  _____ @
                  | |_____|@
                  |    $   @
                  |        @@""".stripMargin,
      "046" -> """|    @
                  |    @
                  |    @
                  |  _ @
                  | (_)@
                  |    @@""".stripMargin,
      "047" -> """|    __@
                  |    / /@
                  |   / / @
                  |  / /  @
                  | /_/   @
                  |       @@""".stripMargin,
      "048" -> """|   ___  @
                  |  / _ \ @
                  | | | | |@
                  | | |_| |@
                  |  \___/ @
                  |        @@""".stripMargin,
      "049" -> """|  _ @
                  | / |@
                  | | |@
                  | | |@
                  | |_|@
                  |    @@""".stripMargin,
      "050" -> """|  ____  @
                  | |___ \ @
                  |   __) |@
                  |  / __/ @
                  | |_____|@
                  |        @@""".stripMargin,
      "051" -> """|  _____ @
                  | |___ / @
                  |   |_ \ @
                  |  ___) |@
                  | |____/ @
                  |        @@""".stripMargin,
      "052" -> """|  _  _   @
                  | | || |  @
                  | | || |_ @
                  | |__   _|@
                  |    |_|  @
                  |         @@""".stripMargin,
      "053" -> """|  ____  @
                  | | ___| @
                  | |___ \ @
                  |  ___) |@
                  | |____/ @
                  |        @@""".stripMargin,
      "054" -> """|   __   @
                  |  / /_  @
                  | | '_ \ @
                  | | (_) |@
                  |  \___/ @
                  |        @@""".stripMargin,
      "055" -> """|  _____ @
                  | |___  |@
                  |    / / @
                  |   / /  @
                  |  /_/   @
                  |        @@""".stripMargin,
      "056" -> """|   ___  @
                  |  ( _ ) @
                  |  / _ \ @
                  | | (_) |@
                  |  \___/ @
                  |        @@""".stripMargin,
      "057" -> """|   ___  @
                  |  / _ \ @
                  | | (_) |@
                  |  \__, |@
                  |    /_/ @
                  |        @@""".stripMargin,
      "058" -> """|    @
                  |  _ @
                  | (_)@
                  |  _ @
                  | (_)@
                  |    @@""".stripMargin,
      "059" -> """|    @
                  |  _ @
                  | (_)@
                  |  _ @
                  | ( )@
                  | |/ @@""".stripMargin,
      "060" -> """|   __@
                  |  / /@
                  | / / @
                  | \ \ @
                  |  \_\@
                  |     @@""".stripMargin,
      "061" -> """|        @
                  |  _____ @
                  | |_____|@
                  | |_____|@
                  |    $   @
                  |        @@""".stripMargin,
      "062" -> """| __  @
                  | \ \ @
                  |  \ \@
                  |  / /@
                  | /_/ @
                  |     @@""".stripMargin,
      "063" -> """|  ___ @
                  | |__ \@
                  |   / /@
                  |  |_| @
                  |  (_) @
                  |      @@""".stripMargin,
      "064" -> """|    ____  @
                  |   / __ \ @
                  |  / / _` |@
                  | | | (_| |@
                  |  \ \__,_|@
                  |   \____/ @@""".stripMargin,
      "065" -> """|     _    @
                  |    / \   @
                  |   / _ \  @
                  |  / ___ \ @
                  | /_/   \_\@
                  |          @@""".stripMargin,
      "066" -> """|  ____  @
                  | | __ ) @
                  | |  _ \ @
                  | | |_) |@
                  | |____/ @
                  |        @@""".stripMargin,
      "067" -> """|   ____ @
                  |  / ___|@
                  | | |    @
                  | | |___ @
                  |  \____|@
                  |        @@""".stripMargin,
      "068" -> """|  ____  @
                  | |  _ \ @
                  | | | | |@
                  | | |_| |@
                  | |____/ @
                  |        @@""".stripMargin,
      "069" -> """|  _____ @
                  | | ____|@
                  | |  _|  @
                  | | |___ @
                  | |_____|@
                  |        @@""".stripMargin,
      "070" -> """|  _____ @
                  | |  ___|@
                  | | |_   @
                  | |  _|  @
                  | |_|    @
                  |        @@""".stripMargin,
      "071" -> """|   ____ @
                  |  / ___|@
                  | | |  _ @
                  | | |_| |@
                  |  \____|@
                  |        @@""".stripMargin,
      "072" -> """|  _   _ @
                  | | | | |@
                  | | |_| |@
                  | |  _  |@
                  | |_| |_|@
                  |        @@""".stripMargin,
      "073" -> """|  ___ @
                  | |_ _|@
                  |  | | @
                  |  | | @
                  | |___|@
                  |      @@""".stripMargin,
      "074" -> """|     _ @
                  |     | |@
                  |  _  | |@
                  | | |_| |@
                  |  \___/ @
                  |        @@""".stripMargin,
      "075" -> """|  _  __@
                  | | |/ /@
                  | | ' / @
                  | | . \ @
                  | |_|\_\@
                  |       @@""".stripMargin,
      "076" -> """|  _     @
                  | | |    @
                  | | |    @
                  | | |___ @
                  | |_____|@
                  |        @@""".stripMargin,
      "077" -> """|  __  __ @
                  | |  \/  |@
                  | | |\/| |@
                  | | |  | |@
                  | |_|  |_|@
                  |         @@""".stripMargin,
      "078" -> """|  _   _ @
                  | | \ | |@
                  | |  \| |@
                  | | |\  |@
                  | |_| \_|@
                  |        @@""".stripMargin,
      "079" -> """|   ___  @
                  |  / _ \ @
                  | | | | |@
                  | | |_| |@
                  |  \___/ @
                  |        @@""".stripMargin,
      "080" -> """|  ____  @
                  | |  _ \ @
                  | | |_) |@
                  | |  __/ @
                  | |_|    @
                  |        @@""".stripMargin,
      "081" -> """|   ___  @
                  |  / _ \ @
                  | | | | |@
                  | | |_| |@
                  |  \__\_\@
                  |        @@""".stripMargin,
      "082" -> """|  ____  @
                  | |  _ \ @
                  | | |_) |@
                  | |  _ < @
                  | |_| \_\@
                  |        @@""".stripMargin,
      "083" -> """|  ____  @
                  | / ___| @
                  | \___ \ @
                  |  ___) |@
                  | |____/ @
                  |        @@""".stripMargin,
      "084" -> """|  _____ @
                  | |_   _|@
                  |   | |  @
                  |   | |  @
                  |   |_|  @
                  |        @@""".stripMargin,
      "085" -> """|  _   _ @
                  | | | | |@
                  | | | | |@
                  | | |_| |@
                  |  \___/ @
                  |        @@""".stripMargin,
      "086" -> """| __     __@
                  | \ \   / /@
                  |  \ \ / / @
                  |   \ V /  @
                  |    \_/   @
                  |          @@""".stripMargin,
      "087" -> """| __        __@
                  | \ \      / /@
                  |  \ \ /\ / / @
                  |   \ V  V /  @
                  |    \_/\_/   @
                  |             @@""".stripMargin,
      "088" -> """| __  __@
                  | \ \/ /@
                  |  \  / @
                  |  /  \ @
                  | /_/\_\@
                  |       @@""".stripMargin,
      "089" -> """| __   __@
                  | \ \ / /@
                  |  \ V / @
                  |   | |  @
                  |   |_|  @
                  |        @@""".stripMargin,
      "090" -> """|  _____@
                  | |__  /@
                  |   / / @
                  |  / /_ @
                  | /____|@
                  |       @@""".stripMargin,
      "091" -> """|  __ @
                  | | _|@
                  | | | @
                  | | | @
                  | | | @
                  | |__|@@""".stripMargin,
      "092" -> """| __    @
                  | \ \   @
                  |  \ \  @
                  |   \ \ @
                  |    \_\@
                  |       @@""".stripMargin,
      "093" -> """|  __ @
                  | |_ |@
                  |  | |@
                  |  | |@
                  |  | |@
                  | |__|@@""".stripMargin,
      "094" -> """|  /\ @
                  | |/\|@
                  |   $ @
                  |   $ @
                  |   $ @
                  |     @@""".stripMargin,
      "095" -> """|        @
                  |        @
                  |        @
                  |        @
                  |  _____ @
                  | |_____|@@""".stripMargin,
      "096" -> """|  _ @
                  | ( )@
                  |  \|@
                  |  $ @
                  |  $ @
                  |    @@""".stripMargin,
      "097" -> """|        @
                  |   __ _ @
                  |  / _` |@
                  | | (_| |@
                  |  \__,_|@
                  |        @@""".stripMargin,
      "098" -> """|  _     @
                  | | |__  @
                  | | '_ \ @
                  | | |_) |@
                  | |_.__/ @
                  |        @@""".stripMargin,
      "099" -> """|       @
                  |   ___ @
                  |  / __|@
                  | | (__ @
                  |  \___|@
                  |       @@""".stripMargin,
      "100" -> """|      _ @
                  |   __| |@
                  |  / _` |@
                  | | (_| |@
                  |  \__,_|@
                  |        @@""".stripMargin,
      "101" -> """|       @
                  |   ___ @
                  |  / _ \@
                  | |  __/@
                  |  \___|@
                  |       @@""".stripMargin,
      "102" -> """|   __ @
                  |  / _|@
                  | | |_ @
                  | |  _|@
                  | |_|  @
                  |      @@""".stripMargin,
      "103" -> """|        @
                  |   __ _ @
                  |  / _` |@
                  | | (_| |@
                  |  \__, |@
                  |  |___/ @@""".stripMargin,
      "104" -> """|  _     @
                  | | |__  @
                  | | '_ \ @
                  | | | | |@
                  | |_| |_|@
                  |        @@""".stripMargin,
      "105" -> """|  _ @
                  | (_)@
                  | | |@
                  | | |@
                  | |_|@
                  |    @@""".stripMargin,
      "106" -> """|    _ @
                  |   (_)@
                  |   | |@
                  |   | |@
                  |  _/ |@
                  | |__/ @@""".stripMargin,
      "107" -> """|  _    @
                  | | | __@
                  | | |/ /@
                  | |   < @
                  | |_|\_\@
                  |       @@""".stripMargin,
      "108" -> """|  _ @
                  | | |@
                  | | |@
                  | | |@
                  | |_|@
                  |    @@""".stripMargin,
      "109" -> """|            @
                  |  _ __ ___  @
                  | | '_ ` _ \ @
                  | | | | | | |@
                  | |_| |_| |_|@
                  |            @@""".stripMargin,
      "110" -> """|        @
                  |  _ __  @
                  | | '_ \ @
                  | | | | |@
                  | |_| |_|@
                  |        @@""".stripMargin,
      "111" -> """|        @
                  |   ___  @
                  |  / _ \ @
                  | | (_) |@
                  |  \___/ @
                  |        @@""".stripMargin,
      "112" -> """|        @
                  |  _ __  @
                  | | '_ \ @
                  | | |_) |@
                  | | .__/ @
                  | |_|    @@""".stripMargin,
      "113" -> """|        @
                  |   __ _ @
                  |  / _` |@
                  | | (_| |@
                  |  \__, |@
                  |     |_|@@""".stripMargin,
      "114" -> """|       @
                  |  _ __ @
                  | | '__|@
                  | | |   @
                  | |_|   @
                  |       @@""".stripMargin,
      "115" -> """|      @
                  |  ___ @
                  | / __|@
                  | \__ \@
                  | |___/@
                  |      @@""".stripMargin,
      "116" -> """|  _   @
                  | | |_ @
                  | | __|@
                  | | |_ @
                  |  \__|@
                  |      @@""".stripMargin,
      "117" -> """|        @
                  |  _   _ @
                  | | | | |@
                  | | |_| |@
                  |  \__,_|@
                  |        @@""".stripMargin,
      "118" -> """|        @
                  | __   __@
                  | \ \ / /@
                  |  \ V / @
                  |   \_/  @
                  |        @@""".stripMargin,
      "119" -> """|           @
                  | __      __@
                  | \ \ /\ / /@
                  |  \ V  V / @
                  |   \_/\_/  @
                  |           @@""".stripMargin,
      "120" -> """|       @
                  | __  __@
                  | \ \/ /@
                  |  >  < @
                  | /_/\_\@
                  |       @@""".stripMargin,
      "121" -> """|        @
                  |  _   _ @
                  | | | | |@
                  | | |_| |@
                  |  \__, |@
                  |  |___/ @@""".stripMargin,
      "122" -> """|      @
                  |  ____@
                  | |_  /@
                  |  / / @
                  | /___|@
                  |      @@""".stripMargin,
      "123" -> """|    __@
                  |   / /@
                  |  | | @
                  | < <  @
                  |  | | @
                  |   \_\@@""".stripMargin,
      "124" -> """|  _ @
                  | | |@
                  | | |@
                  | | |@
                  | | |@
                  | |_|@@""".stripMargin,
      "125" -> """| __   @
                  | \ \  @
                  |  | | @
                  |   > >@
                  |  | | @
                  | /_/  @@""".stripMargin,
      "126" -> """|  /\/|@
                  | |/\/ @
                  |   $  @
                  |   $  @
                  |   $  @
                  |      @@""".stripMargin,
      "196" -> """|  _   _ @
                  | (_)_(_)@
                  |   /_\  @
                  |  / _ \ @
                  | /_/ \_\@
                  |        @@""".stripMargin,
      "214" -> """|  _   _ @
                  | (_)_(_)@
                  |  / _ \ @
                  | | |_| |@
                  |  \___/ @
                  |        @@""".stripMargin,
      "220" -> """|  _   _ @
                  | (_) (_)@
                  | | | | |@
                  | | |_| |@
                  |  \___/ @
                  |        @@""".stripMargin,
      "228" -> """|  _   _ @
                  | (_)_(_)@
                  |  / _` |@
                  | | (_| |@
                  |  \__,_|@
                  |        @@""".stripMargin,
      "246" -> """|  _   _ @
                  | (_)_(_)@
                  |  / _ \ @
                  | | (_) |@
                  |  \___/ @
                  |        @@""".stripMargin,
      "252" -> """|  _   _ @
                  | (_) (_)@
                  | | | | |@
                  | | |_| |@
                  |  \__,_|@
                  |        @@""".stripMargin,
      "223" -> """|   ___ @
                  |  / _ \@
                  | | |/ /@
                  | | |\ \@
                  | | ||_/@
                  | |_|   @@""".stripMargin,
    )

  val codeTag: ListMap[String, String] =
    ListMap(
      "160" -> """|160  NO-BREAK SPACE
                  | $@
                  | $@
                  | $@
                  | $@
                  | $@
                  | $@@""".stripMargin,
      "161" -> """|161  INVERTED EXCLAMATION MARK
                  | _  @
                  | (_)@
                  | | |@
                  | | |@
                  | |_|@
                  |    @@""".stripMargin,
      "162" -> """|162  CENT SIGN
                  |    _  @
                  |   | | @
                  |  / __)@
                  | | (__ @
                  |  \   )@
                  |   |_| @@""".stripMargin,
      "163" -> """|163  POUND SIGN
                  |    ___  @
                  |   / ,_\ @
                  | _| |_   @
                  |  | |___ @
                  | (_,____|@
                  |         @@""".stripMargin,
    )
}
