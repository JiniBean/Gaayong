import type { CategoryItem } from './category'

export interface NamedRef {
  id: string
  name: string
  bank?: string
  issuer?: string
}

export interface ExpenseItem {
  id: string
  name: string
  amt: number | string
  dd: string
  ctg: string
  ctgId: string
  method: string
  acctId?: string
  cardId?: string
}

export interface ExpenseListResponse {
  year: number
  month: number
  total: number
  varTotal: number
  fixedTotal: number
  categoryTotal: number
  list: ExpenseItem[]
  categoryList: CategoryItem[]
  accountList: NamedRef[]
  cardList: NamedRef[]
}

export interface ExpensePayload {
  name: string
  amt: string
  dd: string
  ctgId: string
  acctId?: string
  cardId?: string
  type?: string
}
