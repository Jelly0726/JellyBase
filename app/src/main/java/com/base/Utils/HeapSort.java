package com.base.Utils;

public  class HeapSort {
    public static void main(String[] args) {
        java.util.Scanner sc = new java.util.Scanner(System.in);
        int[] a = new int[100];
        int n = 0;
        Boolean flag = true ;
        while(flag){
            try{
                System.out.println("输入数组：如 1,2,3");
                String str = sc.next().trim();
                if(str.indexOf(",")==0||str.lastIndexOf(",")==str.length()-1)
                    continue;
                String[] strArr = str.split(",");
                n = strArr.length;
                if(n>a.length)
                    continue;
                for(int i = 0; i< strArr.length ; i++)
                    a[i] = new Integer(strArr[i]);
                flag = false;
            }catch(Exception e){
                System.out.println("不合格式，数组格式：1,2,3");
            }
        }
        sc.close();
        System.out.print("      原数组： ");
        arrayToString(a, n);

        //将数组转化成小顶堆
        a = makeMinHeap(a, n);
        System.out.print("变成小顶堆数组： ");
        arrayToString(a, n);
        //将小顶堆，将堆顶与最后的元素互换，节点书减1，然后重新转化成小顶堆，直至堆中没有元素
        for(int i = n; i>1; i--){
            a = minHeapDeleteNumber(a, i);
            arrayToString(a, n);
        }
        System.out.print(" 完成排序数组： ");
        arrayToString(a, n);
    }

    /* 堆的删除 按定义:
     * 堆中每次都只能删除第0个数据。
     * 为了便于重建堆，实际的操作是将最后一个数据的值赋给根结点，
     * 然后再从根结点开始进行一次从上向下的调整。
     * 调整时先在左右儿子结点中找最小的，
     * 如果父结点比这个最小的子结点还小说明不需要调整了，
     * 反之将父结点和它交换后再考虑后面的结点。
     * 相当于从根结点将一个数据的“下沉”过程。*/
    /**
     * 删除小顶堆的堆顶，重新自顶向下，重新建成小顶堆
     * @param a 小顶堆数组
     * @param n 节点个数
     * @return 小顶堆数组
     */
    public static int[] minHeapDeleteNumber(int a[], int n)
    {
        //将a[0]与a[n-1]互换
        int temp = a[0];
        a[0] = a[n-1];
        a[n-1] = temp;

        //节点个数-1
        return minHeapFixdown(a,0,n - 1);
    }
    /**
     * 将以编号i为根节点的子树变成符合小顶堆性质
     * @param a 数组
     * @param i 父节点
     * @param n 节点个数
     * @return 小顶堆数组
     */
    public static int[] minHeapFixdown(int[] a,int i, int n){
        if(n==1) return a;
        int f_node = i;
        //做孩子节点
        int c_node = f_node*2+1;
        //直至孩子节点编号<节点数n,不断循环
        while(c_node<n){
            int temp = a[f_node];

            //如果右节点存在且比左节点小,将childNode变为右子节点的编号
            //孩子节点转化为有孩子节点
            if(c_node+1<n&&a[c_node+1]<a[c_node])
                c_node++;
            //如果孩子节点大于父节点，停止
            if(a[c_node]>=temp)
                break;
            //父节点取孩子节点的值
            a[f_node] = a[c_node];
            a[c_node] = temp;
            //以该孩子节点为父节点
            f_node = c_node;
            c_node = f_node*2+1;
        }
        return a;
    }
    /**
     * 将数组化成二叉堆
     * @param a
     * @param n
     * @return
     */
    public static int[] makeMinHeap(int[] a, int n){
        for(int i = n/2-1; i>=0;i--) {
            a = minHeapFixdown(a, i, n);
            arrayToString(a, n);
        }
        return a;
    }
    public static void arrayToString(int[] a, int n){
        String s = "";
        for(int i = 0; i<n; i++){
            s = s+a[i]+",";
        }
        s = s.substring(0, s.length()-1);
        System.out.println(s);
    }

}
