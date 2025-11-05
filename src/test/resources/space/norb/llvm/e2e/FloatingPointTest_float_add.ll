define float @float_add(float %arg0, float %arg1) {
entry:
  %result = add float %arg0, %arg1
  ret float %result
}