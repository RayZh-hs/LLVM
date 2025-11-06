define i32 @complex_control_flow(i32 %arg0, i32 %arg1) {
entry:
  br label %compute

compute:
  %temp = add i32 %arg0, %arg1
  br label %cleanup

cleanup:
  %result = mul i32 %temp, 2
  br label %exit

exit:
  ret i32 %result
}