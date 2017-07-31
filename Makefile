.PHONY: all

all:
	rm *.class
	rm src/*.class
	javac -g -verbose Main.java

clean:
	rm *.class
	rm src/*.class

new:
	javac -g -verbose Main.java
