mkdir ..\\results\\%4

java -jar dist\CocurrentTreeTests.jar %1 %2 %3 BLTree         -file-..\\results\\%4 -ins%5 -del%6 -special-get%7 -keys%8 -use-range-query
java -jar dist\CocurrentTreeTests.jar %1 %2 %3 SkipList       -file-..\\results\\%4 -ins%5 -del%6 -special-get%7 -keys%8 -use-range-query
::java -jar dist\CocurrentTreeTests.jar %1 %2 %3 BST            -file-..\\results\\%4 -ins%5 -del%6 -special-get%7 -keys%8 -use-range-query
::java -jar dist\CocurrentTreeTests.jar %1 %2 %3 AVL            -file-..\\results\\%4 -ins%5 -del%6 -special-get%7 -keys%8 -use-range-query
java -jar dist\CocurrentTreeTests.jar %1 %2 %3 Snap           -file-..\\results\\%4 -ins%5 -del%6 -special-get%7 -keys%8 -use-range-query


