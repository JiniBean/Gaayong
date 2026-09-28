import type { CategoryItem } from './category'
import type { NamedRef } from './expense'

export interface FixedItem {
  id: string
  name: string
  amt: number | string
  dd?: number | string | null
  ctg: string
  ctgId: string
  method?: string
  acctId?: string
  cardId?: string
  isPaid: boolean | number | string
  isAutoPay?: boolean | number | string
}

export interface FixedListResponse {
  total: number
  unpaid: number
  list: FixedItem[]
  categoryList: CategoryItem[]
  accountList: NamedRef[]
  cardList: NamedRef[]
  filter: string | null
}

export interface FixedPayload {
  name: string
  amt: string
  dd?: string
  ctgId: string
  acctId?: string
  cardId?: string
  type?: string
  isPaid?: string
  isAutoPay?: string
}
