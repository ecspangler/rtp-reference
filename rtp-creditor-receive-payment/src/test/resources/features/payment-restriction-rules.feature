
Feature: RTP Restricted Payments Validation

Example creditor business validations of incoming RTP Credit Transfer Messages


#=======================================================================================================================
Background: 

Given I am a Creditor FI identified by the routing and transit number "020010001"

And I have the following customer bank accounts:
| Account Number    | Account Type | Account Status | Account Holder First Name | Account Holder Last Name |
| 12000194212199001 | Checking     | Open           | Michel                    | Bisset                   |
| 12000194212199002 | Savings      | Open           | Maria                     | Gonzales                 |
| 12000194212199003 | Checking     | Closed         | Antonio                   | Nguyen                   |
| 12000194212199004 | Retirement   | Open           | Sankar                    | Shaw                     |
| 12000194212199005 | Checking     | Blocked        | Catherine                 | Smith                    |

And the current date and time is "2018-11-12T11:27:00" ET
           

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
Scenario: RTP_PRR_001b - Reject Payments higher than 25000
Given I receive the following Credit Transfer Message:
| Message Id                          | Creation Date Time  | Number of Transactions | Payment Amount | Payment Currency | Debtor Id | Creditor Id | Creditor Account  | Settlement Method |
| M2018111511021200201BFFF00000000001 | 2018-11-12T10:05:00 | 1                      | 25000.01       | USD              | 020015555 | 020010001   | 12000194212199001 | CLRG              |

When I validate the Credit Transfer Message

Then I expect the following validation errors:
| Internal Error Code | RTP Reject Reason Code | Error Message              | 
| CPVE_003            | 650                    | Invalid Payment Amount     | 
  


#=======================================================================================================================
Scenario: RTP_PRR_001c - Accept Payments equal to 25000
Given I receive the following Credit Transfer Message:
| Message Id                          | Creation Date Time  | Number of Transactions | Payment Amount | Payment Currency | Debtor Id | Creditor Id | Creditor Account  | Settlement Method |
| M2018111511021200201BFFF00000000001 | 2018-11-12T10:05:00 | 1                      | 25000.00       | USD              | 020015555 | 020010001   | 12000194212199001 | CLRG              |

When I validate the Credit Transfer Message

Then I expect no validation errors




