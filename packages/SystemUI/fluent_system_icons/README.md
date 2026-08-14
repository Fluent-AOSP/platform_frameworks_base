# Microsoft Fluent System Icons subset

This directory records provenance for the curated Fluent System Icons vectors used by Quick Settings.

- Upstream: https://github.com/microsoft/fluentui-system-icons
- Pinned commit: `956fbd8db6c77e62a046eeddb3139354229e9f23`
- License: MIT; see `LICENSE`.
- Source path: `android/library/src/main/res/drawable/`

Imported regular 24 dp vectors:

- `ic_fluent_wifi_4_24_regular.xml`
- `ic_fluent_wifi_off_24_regular.xml`
- `ic_fluent_bluetooth_24_regular.xml`
- `ic_fluent_bluetooth_disabled_24_regular.xml`
- `ic_fluent_cellular_data_1_24_regular.xml`
- `ic_fluent_cellular_off_24_regular.xml`
- `ic_fluent_cast_24_regular.xml`
- `ic_fluent_prohibited_24_regular.xml`
- `ic_fluent_wallet_credit_card_24_regular.xml`
- `ic_fluent_chevron_right_24_regular.xml`
- `ic_fluent_brightness_high_24_regular.xml`
- `ic_fluent_brightness_low_24_regular.xml`
- `ic_fluent_speaker_0_24_regular.xml`
- `ic_fluent_speaker_1_24_regular.xml`
- `ic_fluent_speaker_2_24_regular.xml`
- `ic_fluent_speaker_mute_24_regular.xml`
- `ic_fluent_edit_24_regular.xml`
- `ic_fluent_settings_24_regular.xml`
- `ic_fluent_power_24_regular.xml`
- `ic_fluent_person_24_regular.xml`
- `ic_fluent_chevron_down_24_regular.xml`
- `ic_fluent_options_24_regular.xml`
- `ic_fluent_battery_0_24_regular.xml` through `ic_fluent_battery_10_24_regular.xml`
- `ic_fluent_battery_charge_24_regular.xml`

The vectors are copied without path changes. Their `fluent_default_icon_tint` resource is supplied by SystemUI and is overridden by the existing tile-state tint at render time. The mapping is intentionally limited to known platform tile specs; custom, OEM, and unknown tiles continue to render their supplied icons.
