Feature: RTP Restricted Payments Validation

Example creditor business validations of incoming RTP Credit Transfer Messages



#=======================================================================================================================
Scenario: RTP_PRR_000 - Valid payment 

Given I receive the following Credit Transfer Message:
| Message Id                          | Creation Date Time  | Number of Transactions | Payment Amount | Payment Currency | Debtor Id | Creditor Id | Creditor Account  | Settlement Method |
| M2018111511021200201BFFF00000000001 | 2018-11-12T10:05:00 | 1                      | 512.23         | USD              |           | 020010001   | 12000194212199001 | CLRG              |

When I validate the Credit Transfer Message

Then I expect no validation errors


#=======================================================================================================================
Scenario: RTP_PRR_001a - Allow Payments from Alliant Credit Union below 10,000 -- Routing number 020015555

Given I receive the following Credit Transfer Message:
| Message Id                          | Creation Date Time  | Number of Transactions | Payment Amount | Payment Currency | Debtor Id | Creditor Id | Creditor Account  | Settlement Method |
| M2018111511021200201BFFF00000000001 | 2018-11-12T10:05:00 | 1                      | 9999.99        | USD              | 020015555 | 020010001   | 12000194212199001 | CLRG              |

When I validate the Credit Transfer Message

Then I expect no validation errors


#=======================================================================================================================
Scenario: RTP_PRR_001b - Reject Payments from Alliant Credit Union 10,000 and higher -- Routing number 020015555
Given I receive the following Credit Transfer Message:
| Message Id                          | Creation Date Time  | Number of Transactions | Payment Amount | Payment Currency | Debtor Id | Creditor Id | Creditor Account  | Settlement Method |
| M2018111511021200201BFFF00000000001 | 2018-11-12T10:05:00 | 1                      | 10000.00       | USD              | 020015555 | 020010001   | 12000194212199001 | CLRG              |

When I validate the Credit Transfer Message

Then I expect the following validation errors:
| Internal Error Code | RTP Reject Reason Code | Error Message              | 
| CPVE_010            | 650                    | Restricted payment origin  | 
  