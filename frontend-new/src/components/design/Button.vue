<script lang="ts" setup>
import { computed } from "vue";

const emit = defineEmits<{
  (e: "click"): void;
}>();
const props = withDefaults(
  defineProps<{
    disabled?: boolean;
    size?: "small" | "medium" | "large";
    type?: "primary" | "gray" | "red";
  }>(),
  {
    disabled: false,
    size: "small",
    type: "primary",
  }
);
const paddingClass = computed(() => {
  switch (props.size) {
    default:
    case "small": {
      return "p-1 space-x-0.7";
    }
    case "medium": {
      return "p-2 space-x-0.7";
    }
    case "large": {
      return "p-3 space-x-1.2";
    }
  }
});
</script>

<template>
  <button
    :class="'rounded-md text-white font-semibold h-min inline-flex items-center ' + paddingClass + ' button-' + type"
    :disabled="disabled"
    v-bind="$attrs"
    @click="$emit('click')"
  >
    <slot></slot>
  </button>
</template>
