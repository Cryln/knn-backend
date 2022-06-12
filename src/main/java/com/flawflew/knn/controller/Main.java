package com.flawflew.knn.controller;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        int[] nums = new int[]{1,2,3,4,5,6,7,8};
        Node ans = foo(nums,0,nums.length);
        printing(ans);
    }


    public static Node foo(int[] nums,int start, int end){ //end 不包含
        if(end-start==0){
            return null;
        }
        if(end-start <= 1){
            return new Node(nums[start],null,null);
        }
        int mid = (end+start)/2;
        Node left = foo(nums,start,mid);
        Node right = foo(nums,mid+1,end);
        Node root = new Node(nums[mid],left,right);
        return root;
    }

    static public void printing(Node n){
        if(n==null){
            return;
        }
        System.out.println(n.var);
        printing(n.l);
        printing(n.r);
    }
}

class Node{
    int var;
    Node l;
    Node r;

    public Node(int _v,Node _l,Node _r){
        var = _v;
        l=_l;
        r=_r;
    }
}