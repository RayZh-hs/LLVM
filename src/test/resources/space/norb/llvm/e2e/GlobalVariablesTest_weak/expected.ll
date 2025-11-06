@weak_global = weak global i32 999

define i32 @get_weak_global() {
entry:
  %loaded_value = load i32, ptr @weak_global
  ret i32 %loaded_value
}