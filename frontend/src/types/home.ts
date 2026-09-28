export interface CardPmtItem {
  name: string
  total: number
}

export interface HomeDashboard {
  cash: number
  unpaidFixed: number
  cardPmt: number
  cardPmtList: CardPmtItem[]
  expenseTotal: number
  fixedTotal: number
  budgetTotal: number
  incomeTotal: number
  availAmt: number
  expAvailAmt: number
}
