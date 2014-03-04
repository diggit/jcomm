
MAIN_CLASS=Jimcom.java

JVM=java
JAVAC=javac -Xlint:unchecked

all:
	$(JAVAC) $(MAIN_CLASS)

clean :
	rm -f *.class

run : all
	@$(JVM) $(MAIN_CLASS)
