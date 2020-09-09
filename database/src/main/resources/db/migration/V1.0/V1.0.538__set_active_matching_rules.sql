-- if all matching rules not actives, set active to TRUE, UN-7874
DO $$
BEGIN
  IF NOT EXISTS(SELECT active FROM matching_rules WHERE active = true LIMIT 1) THEN
    UPDATE matching_rules SET active = true;
  END IF;
END$$;