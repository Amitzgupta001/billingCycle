package com.amit.BicycleBilling.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;


public class JSONBeutifier {

    public static void printJson(Object input) throws IOException {

        //System.out.println("input json: " + input);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
        String str = objectMapper.writeValueAsString(input);
        JsonNode rootNode = objectMapper.readTree(str);
        //System.out.printf("root: %s %n", rootNode, rootNode.getNodeType());
        traverse(rootNode, 1);
    }

    private static void traverse(JsonNode node, int level) {
        if (node.getNodeType() == JsonNodeType.ARRAY) {
            traverseArray(node, level);
        } else if (node.getNodeType() == JsonNodeType.OBJECT) {
            traverseObject(node, level);
        }else if (node.getNodeType() == JsonNodeType.BOOLEAN) {
            traverseObject(node, level);
        }
        else {
            throw new RuntimeException("Not yet implemented");
        }
    }

    private static void traverseObject(JsonNode node, int level) {
        node.fieldNames().forEachRemaining((String fieldName) -> {
            JsonNode childNode = node.get(fieldName);
            printNode(childNode, fieldName, level);
            //for nested object or arrays
            if (traversable(childNode)) {
                traverse(childNode, level + 1);
            }
        });
    }

    private static void traverseArray(JsonNode node, int level) {
        int i = 1;
        for (JsonNode jsonArrayNode : node) {
            printNode(jsonArrayNode, i++ + " - arrayElement ", level);
            if (traversable(jsonArrayNode)) {
                traverse(jsonArrayNode, level + 1);
            }
        }
    }

    private static boolean traversable(JsonNode node) {
        return node.getNodeType() == JsonNodeType.OBJECT ||
                node.getNodeType() == JsonNodeType.ARRAY;
    }

    private static void printNode(JsonNode node, String keyName, int level) {
        if (traversable(node)) {
            System.out.printf("%" + (level * 4 - 3) + "s--> %s %n",
                    "", keyName);

        } else {
            Object value = null;
            if (node.isTextual()) {
                value = node.textValue();
            } else if (node.isNumber()) {
                value = node.numberValue();
            }
            else if (node.isBoolean()) {
                value = node.booleanValue();
            }//todo add more types
            System.out.printf("%" + (level * 4 - 3) + "s--> %s : %s %n",
                    "", keyName, value);
        }
    }
}