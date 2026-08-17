import { ref, onMounted, onUnmounted } from "vue";
import type { Ref } from "vue";

interface IntersectionOptions {
  root?: Element | null;
  rootMargin?: string;
  threshold?: number | number[];
}

const useIntersectionObserver = (
  target: Ref<Element | null>,
  options?: IntersectionOptions
) => {
  const isVisible = ref(false);

  const callback: IntersectionObserverCallback = (entries, observer) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        isVisible.value = true;
      } else {
        isVisible.value = false;
      }
    });
  };

  const observer = new IntersectionObserver(callback, options);

  onMounted(() => {
    if (target.value) {
      observer.observe(target.value);
    }
  });

  onUnmounted(() => {
    if (target.value) {
      observer.unobserve(target.value);
    }
  });

  return isVisible;
};

export default useIntersectionObserver;
