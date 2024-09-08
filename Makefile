JAVAC = javac
JAR = jar
JFLAGS = -g
MAINCLASS = Main
SRC = $(wildcard *.java)
CLASSES = $(SRC:.java=.class)
JARFILE = lab1.jar

all: $(JARFILE)

$(JARFILE): $(CLASSES)
	$(JAR) cf $(JARFILE) $(MAINCLASS).class

%.class: %.java
	$(JAVAC) $(JFLAGS) $<

clean:
	rm -f *.class $(JARFILE)