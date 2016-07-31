# drivR
Android App to read from the OBDIIC&amp;C sensor for the Honda Insight.

## WARNING: This project is very alpha and under current development
Also, I do not yet have an OBDIIC&C (it's on order), so this code is based around the documented logging specification for the board, and not yet tested.  If you have an actual OBDIIC&C and would like to test in the meantime, please let me know (iandouglas96@gmail.com)!

## Requirements
* OBDIIC&amp;C (see http://www.insightcentral.net/forums/modifications-technical-issues/20488-obdiic-c-gauge.html)
* Bluetooth Serial dongle (supporting SPP, such as https://www.sparkfun.com/products/12577)

## Getting Started
### Hardware
* Connect the 5V and ground of the dongle to 5V and ground on the OBDIIC&C.  It doesn't particularly matter where you solder on on the PCB, use whatever is convenient for your setup.
* Connect the TX line of the OBDIIC&C to the RX line of the dongle.  Note that TX does NOT go to TX.  TX on the dongle can be left disconnected.

### Software
* Compile drivR in Android Studio and deploy to your phone.
* Pair with the dongle however you normally would in Android.
* Click on "Select Device".
* Choose the donle, and click "Select".
* Click "Open".
* The Current values on your OBDIIC&C display should start displaying in real time in the app.  The update speed should be about 4Hz.
