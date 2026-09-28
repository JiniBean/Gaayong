import type { CategoryItem } from './category'
import type { NamedRef } from './expense'

export interface IncomeItem {
  id: string
  name: string
  amt: number | string
  dd: string
  ctg: string
  ctgId: string
  method?: string
  acctId?: string
}

export interface IncomeListResponse {
  year: number
  month: number
  total: number
  budgetTotal: number
  extraTotal: number
  categoryTotal: number
  list: IncomeItem[]
  categoryList: CategoryItem[]
  accountList: NamedRef[]
}

export interface IncomePayload {
  name: string
  amt: string
  dd: string
  ctgId: string
  acctId?: string
}
