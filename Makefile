all: client server

CLIENT_SRC = $(shell find src/client/ -type f -name '*.java')
SERVER_SRC = $(shell find src/server/ -type f -name '*.java')
BLOCK_LIST = block_list.txt

OUT_DIR = out/

client: out reversi-client.jar

server: $(BLOCK_LIST) out reversi-server.jar

out:
	mkdir -p $@

$(BLOCK_LIST):
	touch $(BLOCK_LIST)

reversi-client.jar: $(addprefix $(OUT_DIR), $(shell javac -d out $(CLIENT_SRC) 2>&1 | grep -Eo "\.\/[^:]*\.java" | grep -Eo "[^\/]*\.class"))
	jar cfe $@ client/Client -C out .

reversi-server.jar: $(addprefix $(OUT_DIR), $(shell javac -d out $(SERVER_SRC) 2>&1 | grep -Eo "\.\/[^:]*\.java" | grep -Eo "[^\/]*\.class"))
	jar cfe $@ server/Server -C out .

$(OUT_DIR)%.class: src/%.java
	javac -d out $<

.PHONY: clean

clean:
	rm -rf out/
	rm -f reversi-client.jar
	rm -f reversi-server.jar
	rm -f $(BLOCK_LIST)