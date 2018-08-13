# Java
Java version of SimplePEG

Using example:

    String grammar = "GRAMMAR url\n" +
            "\n" +
            "url       ->  scheme \"://\" host pathname search hash?;\n" +
            "scheme    ->  \"http\" \"s\"?;\n" +
            "host      ->  hostname port?;\n" +
            "hostname  ->  segment (\".\" segment)*;\n" +
            "segment   ->  [a-z0-9-]+;\n" +
            "port      ->  \":\" [0-9]+;\n" +
            "pathname  ->  \"/\" [^ ?]*;\n" +
            "search    ->  (\"?\" [^ #]*)?;\n" +
            "hash      ->  \"#\" [^ ]*;";
    
    RuleProcessor rp = new RuleProcessor(SpegParser.createAndExec(grammar));
        
    System.out.println(rp.check("https://simplepeg.github.io/"));
    System.out.println(rp.check("https://google.com/"));
    System.out.println(rp.check("https://abcdssss.....com/"));
    