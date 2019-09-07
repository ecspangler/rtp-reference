Feature: RTP Incoming Payments Business Validation

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
Scenario: RTP_CBPV_000 - Valid payment 

Given I receive the following Credit Transfer Message:
| Message Id                          | Creation Date Time  | Number of Transactions | Payment Amount | Payment Currency | Creditor Id | Creditor Account  | Settlement Method |
| M2018111511021200201BFFF00000000001 | 2018-11-12T10:05:00 | 1                      | 512.23         | USD              | 020010001   | 12000194212199001 | CLRG              |

When I validate the Credit Transfer Message

Then I expect no validation errors


#=======================================================================================================================
Scenario: RTP_CBPV_002a - Credit Transfer Message must represent a single transaction - More than one transaction

Given I receive the following Credit Transfer Message:
| Message Id                          | Creation Date Time  | Number of Transactions | Payment Amount | Payment Currency | Creditor Id | Creditor Account  | Settlement Method |
| M2018111511021200201BFFF00000000001 | 2018-11-12T10:05:00 | 4                      | 512.23         | USD              | 020010001   | 12000194212199001 | CLRG              |

When I validate the Credit Transfer Message

Then I expect the following validation errors:
| Internal Error Code | RTP Reject Reason Code | Error Message                                             | 
| CPVE_002            | 650                    | Number of transactions on message was not 1               | 


#=======================================================================================================================
Scenario: RTP_CBPV_002b - Credit Transfer Message must represent a single transaction - No transactions

Given I receive the following Credit Transfer Message:
| Message Id                          | Creation Date Time  | Number of Transactions | Payment Amount | Payment Currency | Creditor Id | Creditor Account  | Settlement Method |
| M2018111511021200201BFFF00000000001 | 2018-11-12T10:05:00 | 0                      | 512.23         | USD              | 020010001   | 12000194212199001 | CLRG              |

When I validate the Credit Transfer Message

Then I expect the following validation errors:
| Internal Error Code | RTP Reject Reason Code | Error Message                                             | 
| CPVE_002            | 650                    | Number of transactions on message was not one             | 


#=======================================================================================================================
Scenario: RTP_CBPV_003 - Payment amount must be positive

Given I receive the following Credit Transfer Message:
| Message Id                          | Creation Date Time  | Number of Transactions | Payment Amount | Payment Currency | Creditor Id | Creditor Account  | Settlement Method |
| M2018111511021200201BFFF00000000001 | 2018-11-12T10:05:00 | 1                      | -512.23        | USD              | 020010001   | 12000194212199001 | CLRG              |

When I validate the Credit Transfer Message

Then I expect the following validation errors:
| Internal Error Code    | RTP Reject Reason Code | Error Message                                             | 
| CPVE_003               | 650                    | Payment amount less than or equal to zero                 | 


#=======================================================================================================================
Scenario: RTP_CBPV_004 - Payment currency must be in US Dollars

Given I receive the following Credit Transfer Message:
| Message Id                          | Creation Date Time  | Number of Transactions | Payment Amount | Payment Currency | Creditor Id | Creditor Account  | Settlement Method |
| M2018111511021200201BFFF00000000001 | 2018-11-12T10:05:00 | 1                      | 512.23         | AUD              | 020010001   | 12000194212199001 | CLRG              |

When I validate the Credit Transfer Message

Then I expect the following validation errors:
| Internal Error Code    | RTP Reject Reason Code | Error Message                                             | 
| CPVE_004               | 650                    | Payment currency code is not valid                        | 


#=======================================================================================================================
Scenario: RTP_CBPV_005 - Incorrect Creditor

Given I receive the following Credit Transfer Message:
| Message Id                          | Creation Date Time  | Number of Transactions | Payment Amount | Payment Currency | Creditor Id | Creditor Account  | Settlement Method |
| M2018111511021200201BFFF00000000001 | 2018-11-12T10:05:00 | 1                      | 512.23         | USD              | 020010002   | 12000194212199001 | CLRG              |

When I validate the Credit Transfer Message

Then I expect the following validation errors:
| Internal Error Code    | RTP Reject Reason Code | Error Message                                             | 
| CPVE_005               | BE17                   | Creditor identification is not valid                      | 


#=======================================================================================================================
Scenario: RTP_CBPV_006 - Account to be credited must exist 

Given I receive the following Credit Transfer Message:
| Message Id                          | Creation Date Time  | Number of Transactions | Payment Amount | Payment Currency | Creditor Id | Creditor Account  | Settlement Method |
| M2018111511021200201BFFF00000000001 | 2018-11-12T10:05:00 | 1                      | 512.23         | USD              | 020010001   | 12000194212199017 | CLRG              |

When I validate the Credit Transfer Message

Then I expect the following validation errors:
| Internal Error Code | RTP Reject Reason Code | Error Message                                             | 
| CPVE_006            | AC03                   | Account number is not valid                               | 


#=======================================================================================================================
Scenario: RTP_CBPV_009 - Settlement Method must be CLRG

Given I receive the following Credit Transfer Message:
| Message Id                          | Creation Date Time  | Number of Transactions | Payment Amount | Payment Currency | Creditor Id | Creditor Account  | Settlement Method |
| M2018111511021200201BFFF00000000001 | 2018-11-12T10:05:00 | 1                      | 512.23         | USD              | 020010001   | 12000194212199001 | CLRD              |

When I validate the Credit Transfer Message

Then I expect the following validation errors:
| Internal Error Code | RTP Reject Reason Code | Error Message                                             | 
| CPVE_009            | 650                    | Settlement method is invalid                              | 


#=======================================================================================================================
Scenario: RTP_CBPV_000 - Multiple validation errors

Given I receive the following Credit Transfer Message:
| Message Id                          | Creation Date Time  | Number of Transactions | Payment Amount | Payment Currency | Creditor Id | Creditor Account  | Settlement Method |
| M2018111511021200201BFFF00000000001 | 2017-11-12T10:05:00 | -3                     | 0.00           | GBP              | 020010001   | 12000194212199011 | CLRG              |

When I validate the Credit Transfer Message

Then I expect the following validation errors:
| Internal Error Code | RTP Reject Reason Code | Error Message                                             | 
| CPVE_002            | 650                    | Number of transactions on message was not 1               | 
| CPVE_003            | 650                    | Payment amount less than or equal to zero                 | 
| CPVE_004            | 650                    | Payment currency code is not valid                        | 
| CPVE_006            | AC03                   | Account number is not valid                               | 

  