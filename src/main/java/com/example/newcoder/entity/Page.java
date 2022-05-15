package com.example.newcoder.entity;

@SuppressWarnings({"all"})

public class Page {
    // 分页相关的信息

    //
    private int current=1;  // 默认页
    private int limit=10;   // 每页的上限
    private int rows;  // 用于计算总页数
    private String path;    // 用于复用页面链接



    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current>=1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if( limit>1 && limit<100){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOffset(){
        // 起始行= (current-1)*limit
        return (current-1)*limit;
    }

    public int getTotal(){
        // 总页数
        return rows/limit +1;
    }

    /**
     * 获取起始页码
     *
     * @return
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取结束页码
     *
     * @return
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
