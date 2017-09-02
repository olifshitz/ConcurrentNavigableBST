mkdir ..\\results\\%4

java -jar dist\CocurrentTreeTests.jar %1 %2 %3 BLTree         -file-..\\results\\%4 -ins%5 -del%6 -range%7 -keys%8 -range-size%9
java -jar dist\CocurrentTreeTests.jar %1 %2 %3 SkipList       -file-..\\results\\%4 -ins%5 -del%6 -range%7 -keys%8 -range-size%9
java -jar dist\CocurrentTreeTests.jar %1 %2 %3 SyncTMAP       -file-..\\results\\%4 -ins%5 -del%6 -range%7 -keys%8 -range-size%9
java -jar dist\CocurrentTreeTests.jar %1 %2 %3 AVL            -file-..\\results\\%4 -ins%5 -del%6 -range%7 -keys%8 -range-size%9
java -jar dist\CocurrentTreeTests.jar %1 %2 %3 Snap           -file-..\\results\\%4 -ins%5 -del%6 -range%7 -keys%8 -range-size%9


