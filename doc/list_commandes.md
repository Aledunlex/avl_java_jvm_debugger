# Commandes

1. `step` : execute la prochaine instruction. S’il s’agit d’un appel de méthode, l’exécution entre dans cette dernière.

2. `step-over` : execute la ligne courante.

3. `continue` : continue l’exécution jusqu’au prochain point d’arrêt. La granularité est l’instruction `step`.

4. `frame` : renvoie et imprime la frame courante.

5. `temporaries` : renvoie et imprime la liste des variables temporaires de la frame courante, sous la forme de couples
   nom → valeur.

6. `stack` : renvoie la pile d’appel de méthodes qui a amené l’exécution au point courant.

7. `receiver` : renvoie le receveur de la méthode courante (`this`).

8. `sender` : renvoie l’objet qui a appelé la méthode courante.

9. `receiver-variables` : renvoie et imprime la liste des variables d’instance du receveur courant, sous la forme d’un
   couple nom → valeur .

10. `method` : renvoie et imprime la méthode en cours d’exécution.

11. `arguments` : renvoie et imprime la liste des arguments de la méthode en cours d’exécution, sous la forme d’un
    couple nom → valeur.

12. `print-var(String varName)` : imprime la valeur de la variable passée en paramètre.

13. `break(String filename, int lineNumber)` : installe un point d’arrêt à la ligne `lineNumber` du fichier `fileName`.

14. `breakpoints` : liste les points d’arrêts actifs et leurs location dans le code.

15. `break-once(String filename, int lineNumber)` : installe un point d’arrêt à la ligne `lineNumber` du
    fichier `fileName`. Ce point d’arrêt se désinstalle après avoir été atteint.

16. `break-on-count(String filename, int lineNumber, int count)` : installe un point d’arrêt à la ligne `lineNumber` du
    fichier `fileName`. Ce point d’arrêt ne s’active qu’après avoir été atteint un certain nombre de fois `count`.

17. `break-before-method-call(String methodName)` : configure l’exécution pour s’arrêter au tout début de l’exécution de
    la méthode `methodName`.
