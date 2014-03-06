

SRC_PATH=jimcom/
BUILD_PATH=build/
MAIN_CLASS=Jimcom

JVM=java
JAVAC=javac -Xlint:unchecked

all:
	$(JAVAC) -d $(BUILD_PATH) $(SRC_PATH)$(MAIN_CLASS).java

clean :
	rm -f $(BUILD_PATH)*.class

run : all
	cd $(BUILD_PATH) && $(JVM) $(SRC_PATH)$(MAIN_CLASS)
