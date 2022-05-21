package com.example.newcoder.util;

import org.apache.tomcat.util.buf.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;
import org.apache.commons.lang3.CharUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
@SuppressWarnings({"all"})
@Component
public class SensitiveFilter {
    private static final Logger logger= LoggerFactory.getLogger(SensitiveFilter.class);
    // 替换符号
    private static final String REPLACEMENT="***";
    // 根节点
    TrieNode rootNode=new TrieNode();

    // 根据敏感词初始前缀树
    @PostConstruct  //干嘛的
    public void init(){
        try (// 用定义的敏感词文件，初始化前缀树
                final InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("sensitiveWord.txt");
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));){
            String keyword;
            while ((keyword = bufferedReader.readLine())!=null){
                // 从文件中按行读取，降每个词都填入到前缀树中
                this.addKeyword(keyword);
            }

        }catch (IOException e){
            logger.error("加载敏感词文件失败，"+e.getMessage());
        }
    }

    // 根据敏感词，处理前缀树
    private void addKeyword(String keyword){
        TrieNode tmpNode=rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c=keyword.charAt(i);
            TrieNode subNode = tmpNode.getSubNode(c);
            if (subNode==null){
                // 初始化子节点
                subNode = new TrieNode();
                tmpNode.addSubNode(c,subNode);
            }
            // 指向子节点，进入下一轮循环
            tmpNode=subNode;
            // 设置结束标识,即把敏感词遍历完了
            if(i==keyword.length()-1){
                tmpNode.setKeywordEnd(true);
            }
        }
    }

    // 定义前缀树
    private class TrieNode{
        // 关键词结束标识
        private boolean isKeywordEnd=false;

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        // 子节点容器，因为一个节点的子节点会有多个，所以需要使用容器才存储
        private Map<Character,TrieNode> subNodes=new HashMap<>();

        // 添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }
        // 获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }


    // 编写 敏感词 过滤方法，返回的是过滤后的结果
    public String filter(String text){
        // 空值判断
        if (StringUtils.isEmptyOrWhitespace(text)){
            return null;
        }
        // 不为空的情况，开始对文本进行过滤处理；用三个指针，分别指向的是前缀树、文本；
        // 在文本中，相当于用双指针，找到前缀树中出现的路径，然后对双指针指向的区域进行文本替换
        TrieNode tempNode= rootNode; // 指针 1
        int begin=0,position=0;  // 指针2 3
        // 结果
        StringBuilder sb=new StringBuilder();


        while(position < text.length()){

            Character c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    begin++;
                    sb.append(c);
                }
                position++;
                continue;
            }
            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            }
            // 发现敏感词
            else if (tempNode.isKeywordEnd()) {
                sb.append(REPLACEMENT);
                begin = ++position;
            }
            // 检查下一个字符
            else {
                position++;
            }

            // 提前判断postion是不是到达结尾，要跳出while,如果是，则说明begin-position这个区间不是敏感词，但是里面不一定没有
            if (position==text.length() && begin!=position){
                // 说明还剩下一段需要判断，则把position==++begin
                // 并且当前的区间的开头字符是合法的
                sb.append(text.charAt(begin));
                position=++begin;
                tempNode=rootNode;  // 前缀表从头开始了
            }
        }
        return sb.toString();
    }

    // 过滤文本中的符号，如 *嫖**娼*
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


}
