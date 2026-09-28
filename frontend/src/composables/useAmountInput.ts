import { ref, type Ref } from 'vue'
import { formatAmount, stripAmount } from '../utils/formatAmount'

export function useAmountInput(initial = '') {
  const display = ref(initial)

  function onFocus() {
    display.value = stripAmount(display.value)
  }

  function onBlur() {
    if (display.value === '') return
    display.value = formatAmount(display.value)
  }

  function onInput() {
    display.value = display.value.replace(/[^0-9]/g, '')
  }

  function valueForSubmit(): string {
    return stripAmount(display.value)
  }

  function setFromRaw(raw: number | string | null | undefined) {
    display.value = raw != null && raw !== '' ? formatAmount(raw) : ''
  }

  return { display, onFocus, onBlur, onInput, valueForSubmit, setFromRaw }
}

export function bindAmountInput(
  display: Ref<string>,
  onFocus: () => void,
  onBlur: () => void,
  onInput: () => void,
) {
  return {
    value: display,
    onFocus,
    onBlur,
    onInput,
  }
}
