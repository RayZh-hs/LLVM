define i32 @complex_comparisons(i32 %arg0, i32 %arg1, i32 %arg2) {
entry:
  %cmp1 = icmp sgt i32 %arg0, %arg1
  %cmp2 = icmp ult i32 %arg1, %arg2
  %condition = and i1 %cmp1, %cmp2
  br i1 %condition, label %then, label %else

then:
  %then_result = add i32 %arg0, %arg2
  br label %merge

else:
  %else_result = sub i32 %arg0, %arg2
  br label %merge

merge:
  %result = phi i32 [ %then_result, %then ], [ %else_result, %else ]
  ret i32 %result
}