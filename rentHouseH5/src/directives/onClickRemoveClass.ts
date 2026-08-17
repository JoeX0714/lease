import type { Directive, DirectiveBinding } from "vue";
/**
 * @description 📚 点击元素移除指定类名
 * @param  {array} 需要移除类名的数组
 * @docs  v-remove-class 其中repeat表示元素离开可视区域后会自动将添加的类名卸载掉
 * 使用方式，在main.js中引入并注册
 * import inViewDirective from './direct/in-view-directive'
 * app.directive('in-view', inViewDirective);
 * 页面内直接使用
 *    <div v-remove-class="['class1', 'class2']">点 击我去除 class1 和 class2</div>
 **/

const removeClassDirective: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const classNames = binding.value;
    const handleClick = (event: MouseEvent) => {
      if (event.target instanceof Element && classNames) {
        if (Array.isArray(classNames)) {
          classNames.forEach(className => {
            el.classList.remove(className);
          });
        } else {
          el.classList.remove(classNames);
        }
      }
    };

    el.addEventListener("click", handleClick);
  }
};

export default removeClassDirective;
